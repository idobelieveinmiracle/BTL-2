/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.team6.runs;

import com.team6.common.User;
import com.team6.views.HomeForm;
import com.team6.views.SignUpForm;
import java.util.ArrayList;

/**
 *
 * @author Quoc Hung
 */
public class TestRun {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        HomeForm hmForm = new HomeForm();
        hmForm.setUser(new User("ztr", "123", "Nguyen Quoc Hung", 1000, 0));
        hmForm.setVisible(true);
        
        ArrayList<User> list = new ArrayList<>();
        
        list.add(new User("aksjd", "asd", "asd", 12, 0));
        list.add(new User("aksjd", "asd", "asd", 12, 0));
        list.add(new User("aksjd", "asd", "asd", 12, 0));
        list.add(new User("aksjd", "asd", "asd", 12, 0));
        list.add(new User("aksjd", "asd", "asd", 12, 0));
        
        hmForm.setListUsers(list);
    }
    
}
