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

    Map<Integer, List<Booking>> listOfBookingsMap;



    public HotelManagementRepository() {
        this.hotelMap = new HashMap<String,Hotel>();
        this.listOfBookingsMap = new HashMap<Integer,List<Booking>>();
        this.userMap = new HashMap<Integer,User>();
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

        int count =0;
        String s1="";
        for(Hotel hotel:hotelMap.values()) {
            if (hotel.getFacilities().size() > count) {
                count = hotel.getFacilities().size();
                s1 = hotel.getHotelName();
            }
            // s1 panner hotel.get = butter
            if (hotel.getFacilities().size() == count && s1.compareTo(hotel.getHotelName()) > 0) {
                s1 = hotel.getHotelName();
            }
            if (hotel.getFacilities().size() == count && s1.compareTo(hotel.getHotelName()) < 0) {
                s1 = s1;
            }
        }
        return s1;
    }

//    public String lexicography(String s1,String s2,int a) {
//        int length1 = s1.length();
//        int length2 = s2.length();
//
//        if (s1.charAt(a) > s2.charAt(a) || a>length2)
//            return s2;
//        if (s1.charAt(a) < s2.charAt(a) || a>length1)
//            return s1;
//        else {
//            a++;
//            return lexicography(s1, s2, a);
//        }
//    }

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
            if(listOfBookingsMap.get(user.getaadharCardNo())==null || !listOfBookingsMap.containsKey(user.getaadharCardNo())){
                List<Booking> bookingList = new ArrayList<>();
                bookingList.add(booking);
                listOfBookingsMap.put(user.getaadharCardNo(),bookingList);
            }
            listOfBookingsMap.get(user.getaadharCardNo()).add(booking);
            return booking.getAmountToBePaid();
        }
        return -1;
    }


    public int getBookings(Integer aadharCard)
    {
        //In this function return the bookings done by a person

        User user = userMap.get(aadharCard);
       return listOfBookingsMap.get(aadharCard).size();

        }

    public Hotel updateFacilities(List<Facility> newFacilities, String hotelName){

        //We are having a new facilites that a hotel is planning to bring.
        //If the hotel is already having that facility ignore that facility otherwise add that facility in the hotelDb
        //return the final updated List of facilities and also update that in your hotelDb
        //Note that newFacilities can also have duplicate facilities possible

         Hotel hotel = hotelMap.get(hotelName);
         if(hotel.getFacilities().size()==0) {
             hotel.setFacilities(newFacilities);
             return hotel;
         }
         for (Facility facility:newFacilities){
             boolean flag = false;
             for (Facility facility1:hotel.getFacilities()){
                 if(facility1==facility){
                     flag = true;
                     break;
                 }
             }
             if(flag==false)
             hotel.getFacilities().add(facility);
         }
  return  hotel;
    }
}
