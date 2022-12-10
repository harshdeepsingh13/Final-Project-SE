package com.example.finalproject.web;

import com.example.finalproject.entities.Customer;
import com.example.finalproject.repositories.CustomerRepository;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.util.List;
import java.util.Map;

@Controller
@AllArgsConstructor
@SessionAttributes({"alertBox"})
public class CustomerController {

    @Autowired
    private CustomerRepository customerRepository;


    @GetMapping("/")
    public String getHome(Model model) {
        model.addAttribute("customer", new Customer());
        List<Customer> customers = customerRepository.findAll();
        Map<String, String> reservedSeats = customerRepository.findReservedCustomers();
        System.out.println("###################" + reservedSeats);
        model.addAttribute("reservedSeats", reservedSeats);
        model.addAttribute("listCustomers", customers);
        model.addAttribute("remainingSeats", "There are " + (20 - customers.size()) + " remaining seats");
        return "home";
    }

    @GetMapping("/index")
    public String getHome2(Model model) {
        model.addAttribute("customer", new Customer());
        List<Customer> customers = customerRepository.findAll();
        Map<String, String> reservedSeats = customerRepository.findReservedCustomers();
        System.out.println("###################" + reservedSeats);
        model.addAttribute("reservedSeats", reservedSeats);
        model.addAttribute("listCustomers", customers);
        model.addAttribute("remainingSeats", "There are " + (20 - customers.size()) + " remaining seats");
        return "home";
    }

    @PostMapping("/reserve")
    public String reserveSeat(Model model, Customer customer, BindingResult bindingResult, ModelMap modelMap) {
        Map<String, String> reservedSeats = customerRepository.findReservedCustomers();
        System.out.println("####################"+customer);
        if (bindingResult.hasErrors()) return "redirect:/";
        else {
            String seatNumber = customer.getSeatno();
            if (!verifySeatNumber(seatNumber)) {
                modelMap.put("alertBox", "Please follow the seat code format");
            } else if (reservedSeats.containsKey(customer.getSeatno())) {
                modelMap.put("alertBox", "The seat is already taken");
            } else {
                customerRepository.save(customer);
                modelMap.put("alertBox", "Seat Reserved!");
            }
        }
        return "redirect:/";
    }

    @PostMapping("/save")
    public String saveEdit(Model model, Customer customer, BindingResult bindingResult, ModelMap modelMap) {
        Map<String, String> reservedSeats = customerRepository.findReservedCustomers();
        reservedSeats.remove(customer.getSeatno());
        if (bindingResult.hasErrors()) return "redirect:/";
        else {
            String seatNumber = customer.getSeatno();
            if (!verifySeatNumber(seatNumber)) {
                modelMap.put("alertBox", "Please follow the seat code format");
                return "redirect:/editRecord?id="+customer.getId();
            } else if (reservedSeats.containsKey(customer.getSeatno())) {
                modelMap.put("alertBox", "The seat is already taken");
                return "redirect:/editRecord?id="+customer.getId();
            } else {
                customerRepository.save(customer);
                modelMap.put("alertBox", "Update saved!");
            }
        }
        return "redirect:/";
    }

    @GetMapping("/delete")
    public String delete(Model model, Long id) {
        customerRepository.deleteById(id);
        model.addAttribute("alertBox", "Record deleted successfully");
        return "redirect:/";
    }

    @GetMapping("/editRecord")
    public String editRecord(Model model, Long id, HttpSession session) {
        session.setAttribute("info", 0);
        Customer customer = customerRepository.findById(id).orElse(null);
        if (customer == null) throw new RuntimeException("Record does not exist");
        model.addAttribute("customer", customer);
        return "editRecord";
    }

    private boolean verifySeatNumber(String seatNumber) {
        if (seatNumber.length() != 2) return false;
        char firstLetter = seatNumber.charAt(0);
        char secondLetter = seatNumber.charAt(1);
        if ((!Character.isDigit(firstLetter)) || (Integer.parseInt(String.valueOf(firstLetter)) < 1 || Integer.parseInt(String.valueOf(firstLetter)) > 4))
            return false;
        if ((!Character.isLetter(secondLetter)) || (secondLetter < 'A' || secondLetter > 'E')) return false;
        return true;
    }
}
