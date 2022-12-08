package com.ltp.globalsuperstore;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Controller
public class SuperstoreController {

    List<Item> items = new ArrayList<Item>();
    //Item item = new Item();

    @GetMapping("/")
    public String getForm(Model model, @RequestParam(required = false) String id){
        int index = getIndexId(id);
        if (index == Constants.NO_MATCH){
            model.addAttribute("item",new Item());
        } else {
            model.addAttribute("item",items.get(index));
        }
        model.addAttribute("categories",Constants.CATEGORIES);
        return "form";
    }

    @GetMapping("/inventory")
    public String getInventory(Model model){
        model.addAttribute("items",items);
        return "inventory";
    }

    @PostMapping("/submitItem")
    public String handleSubmit(Item item, RedirectAttributes redirectAttributes){
        int index = getIndexId(item.getId());

        if (index == Constants.NO_MATCH){
            items.add(item);
            redirectAttributes.addFlashAttribute("status",Constants.SUCCESS_STATUS);
        } else {
            items.set(index,item);
            if (within5Days(items.get(index).getDate(),item.getDate())){
                redirectAttributes.addFlashAttribute("status",Constants.SUCCESS_STATUS);
            } else {
                redirectAttributes.addFlashAttribute("status",Constants.FAILED_STATUS);
            }
        }

        return "redirect:/inventory";
    }

    public int getIndexId(String id){
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getId().equals(id)){
                return i;
            }
        } return Constants.NO_MATCH;
    }

    public boolean within5Days(Date date1,Date date2){
        long diff = Math.abs(date1.getTime() - date2.getTime());
        return (int) (TimeUnit.MILLISECONDS.toDays(diff)) <= 5;
    }
}
