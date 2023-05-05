package Repositories;

import com.driver.model.Booking;
import com.driver.model.Facility;
import com.driver.model.Hotel;
import com.driver.model.User;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.awt.print.Book;
import java.util.*;

@Repository
public class HotelManagementRepository {


    Map<String,Hotel> hotelMap;

    Map<Integer,User> userMap;

    Map<String, List<Booking>> listOfBookingsMap;

    Map<String,List<Facility>>  hotelFacilityMap;

    public HotelManagementRepository() {
        this.hotelMap = new HashMap<>();
        this.listOfBookingsMap = new HashMap<>();
        this.userMap = new HashMap<>();
        this.hotelFacilityMap = new HashMap<>();
    }

    public String addHotel(Hotel hotel) {

        //You need to add an hotel to the database
        //incase the hotelName is null or the hotel Object is null return an empty a FAILURE
        //Incase somebody is trying to add the duplicate hotelName return FAILURE
        //in all other cases return SUCCESS after successfully adding the hotel to the hotelDb.

        if (hotelMap.containsKey(hotel.getHotelName()) || hotel == null || hotel.getHotelName()==null)
            return "FAILURE";

              hotelMap.put(hotel.getHotelName(),hotel);
            return "SUCCESS";
    }

    public Integer addUser(User user){

        //You need to add a User Object to the database
        //Assume that user will always be a valid user and return the aadharCardNo of the user
     userMap.put(user.getaadharCardNo(),user);
        return user.getaadharCardNo();
    }

    public String getHotelWithMostFacilities() {

        //Out of all the hotels we have added so far, we need to find the hotelName with most no of facilities
        //Incase there is a tie return the lexicographically smaller hotelName
        //Incase there is not even a single hotel with atleast 1 facility return "" (empty string)
        Hotel hotel = new Hotel();
        int listSize = 0;
        String s1 = "";
        for (String s : hotelMap.keySet()) {
            if (hotelMap.get(s).getFacilities().size() > listSize) {
                listSize = hotelMap.get(s).getFacilities().size();
                s1 = s;
            }
            if (hotelMap.get(s).getFacilities().size() == listSize) {
                s1 = lexicography(s1, s, 0);
            }
        }
        return s1;
    }

    public String lexicography(String s1,String s2,int a) {
        if (s1.charAt(a) > s2.charAt(a))
            return s2;
        if (s1.charAt(a) < s2.charAt(a))
            return s1;
        else {
            a++;
            return lexicography(s1, s2, a);
        }
    }

    public int bookARoom(Booking booking){

        //The booking object coming from postman will have all the attributes except bookingId and amountToBePaid;
        //Have bookingId as a random UUID generated String
        //save the booking Entity and keep the bookingId as a primary key
        //Calculate the total amount paid by the person based on no. of rooms booked and price of the room per night.
        //If there arent enough rooms available in the hotel that we are trying to book return -1
        //in other case return total amount paid
        Hotel hotel = hotelMap.get(booking.getHotelName());
        User user = null;
       for (int i : userMap.keySet()){
           if(userMap.get(i).getName().equals(booking.getBookingPersonName()))
               user = userMap.get(i);
       }
        if(hotel.getAvailableRooms()>=booking.getNoOfRooms()){
            booking.setBookingId(UUID.randomUUID().toString());
            booking.setAmountToBePaid(booking.getNoOfRooms()*hotel.getPricePerNight());
            hotel.setAvailableRooms(hotel.getAvailableRooms()-booking.getNoOfRooms());
            if(listOfBookingsMap.get(user.getName())==null || !listOfBookingsMap.containsKey(user.getName())){
                List<Booking> bookingList = new ArrayList<>();
                bookingList.add(booking);
                listOfBookingsMap.put(user.getName(),bookingList);
            }
            listOfBookingsMap.get(user.getName()).add(booking);
            return booking.getAmountToBePaid();
        }
        return -1;
    }


    public int getBookings(Integer aadharCard)
    {
        //In this function return the bookings done by a person

        User user = userMap.get(aadharCard);
        List<Booking> bookingList=new ArrayList<>();
        int count=0;
        for(String s: listOfBookingsMap.keySet()) {
            count = listOfBookingsMap.get(user.getName()).size();

        }
            return count;
        }

    public Hotel updateFacilities(List<Facility> newFacilities, String hotelName){

        //We are having a new facilites that a hotel is planning to bring.
        //If the hotel is already having that facility ignore that facility otherwise add that facility in the hotelDb
        //return the final updated List of facilities and also update that in your hotelDb
        //Note that newFacilities can also have duplicate facilities possible
          List<Facility> oldFacilities = hotelFacilityMap.get(hotelName);

          for (Facility facility:newFacilities){
              boolean flag = false;
              for (Facility facility1:oldFacilities) {
                  if (facility1 == facility) {
                      flag = true;
                  }
              }
              if(flag==false)
                  oldFacilities.add(facility);
          }
          return hotelMap.get(hotelName);

    }
}
