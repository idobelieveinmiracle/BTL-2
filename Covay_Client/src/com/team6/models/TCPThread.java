/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.team6.models;

import com.team6.common.ChessBoard;
import com.team6.common.Message;
import com.team6.common.User;
import com.team6.views.GameFrame;
import com.team6.views.GamePanel;
import com.team6.views.HomeForm;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author Quoc Hung
 */
public class TCPThread extends Thread{
    private HomeForm homeForm;
    private User user;
    private String enemyName;
    private Socket socket;

    public TCPThread(HomeForm homeForm, User user, Socket socket) {
        this.homeForm = homeForm;
        this.user = user;
        this.socket = socket;
        
        this.homeForm.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e){
                try {
                    socket.close();
                } catch (IOException ex) {
                    Logger.getLogger(TCPThread.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }
    
    @Override
    public void run(){
        System.out.println("Started TCP Thread");
        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;
        try {
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());
            System.out.println("created oos, ois");
            
            oos.writeObject(new Message("Login", user));
            System.out.println("Sent message Login with username: "+user.getUsername());
            
            while (true){
                Object o = null;
                
                try {
                    o = ois.readObject();
                } catch (EOFException e){
                    break;
                }
                
                if (o instanceof Message){
                    Message message = (Message) o;
                    
                    if (message.getTitle().equals("CheckOnline")) {
                        oos.writeObject(new Message("ON", null));
                        continue;
                    }
                    
                    if (message.getTitle().equals("Invite")){
                        if (JOptionPane.showConfirmDialog(homeForm, 
                                message.getContent().toString()+
                                " muốn so tài với cậu, cậu có đồng ý không?")==0){
                            oos.writeObject(new Message("AC", null));
                            System.out.println("Playing...");
                        } else oos.writeObject(new Message("DE", null));
                    }
                    
                    if (message.getTitle().equals("OpenMatch")){
                        GamePanel gamePanel = new GamePanel(new ChessBoard());
                        GameFrame gameFrame = new GameFrame(gamePanel);
                        
                        gameFrame.addWindowListener(new WindowAdapter() {
                            @Override
                            public void windowClosing(WindowEvent e){
                                homeForm.setVisible(true);
                            }
                        });
                        
                        homeForm.setVisible(false);
                        gameFrame.setVisible(true);
                        if (message.getContent().equals("player1")){
                            gamePanel.setSide(ChessBoard.WHITE);
                            ChessBoard chessBoard;
                            
                            long FPS = 80;                      //Frames per Seconds
                            long period = 1000*1000000/FPS;     //Tru kì
                            long beginTime;
                            long sleepTime;

                            int a = 1;

                            beginTime = System.nanoTime();      //nanoTime() là hàm lấy thời gian hệ thống  
                            
                            while (true){                                
                                chessBoard = gamePanel.getChessBoard();
                                System.out.println("Player 1 is running..."+chessBoard.getTurn());
                                if (chessBoard.getTurn() == ChessBoard.BLACK){
                                    System.out.println("Turn: "+chessBoard.getTurn());
                                    
                                    oos.writeObject(new Message("Move", chessBoard));
                                    System.out.println("Sent move player 1 reuqest to server");
                                    
                                    Message player1RecievedMessage = (Message) ois.readObject();
                                    if (player1RecievedMessage.getTitle().equals("Move")){
                                        chessBoard = (ChessBoard) player1RecievedMessage.getContent();
                                        gamePanel.setChessBoard(chessBoard);
                                    }
                                }
                                
                                long deltaTime = System.nanoTime() - beginTime;
                                sleepTime = period - deltaTime;

                                try {
                                    if(sleepTime > 0)
                                        Thread.sleep(sleepTime/1000000);
                                    else Thread.sleep(period/2000000);
                                } catch (InterruptedException ex) {

                                }            
                                beginTime = System.nanoTime();
                            }
                        }else {
                            gamePanel.setSide(ChessBoard.BLACK);
                            ChessBoard chessBoard;
                            
                            long FPS = 80;                      //Frames per Seconds
                            long period = 1000*1000000/FPS;     //Tru kì
                            long beginTime;
                            long sleepTime;

                            int a = 1;

                            beginTime = System.nanoTime(); 
                            
                            Message player2RecievedMessage1 = (Message) ois.readObject();
                            System.out.println("Recieved move response");
                            if (player2RecievedMessage1.getTitle().equals("Move")){
                                chessBoard = (ChessBoard) player2RecievedMessage1.getContent();
                                gamePanel.setChessBoard(chessBoard);
                            }
                            
                            while (true){
                                chessBoard = gamePanel.getChessBoard();
                                System.out.println("Player 2 is running..."+chessBoard.getTurn());
                                if (chessBoard.getTurn() == ChessBoard.WHITE){
                                    oos.writeObject(new Message("Move", chessBoard));
                                    System.out.println("Sent move player 2 reuqest to server");
                                    
                                    Message player2RecievedMessage = (Message) ois.readObject();
                                    if (player2RecievedMessage.getTitle().equals("Move")){
                                        chessBoard = (ChessBoard) player2RecievedMessage.getContent();
                                        gamePanel.setChessBoard(chessBoard);
                                    }
                                }
                                
                                long deltaTime = System.nanoTime() - beginTime;
                                sleepTime = period - deltaTime;

                                try {
                                    if(sleepTime > 0)
                                        Thread.sleep(sleepTime/1000000);
                                    else Thread.sleep(period/2000000);
                                } catch (InterruptedException ex) {

                                }            
                                beginTime = System.nanoTime();
                            }
                        }
                    }
                }
            }      
            
        } catch (IOException ex) {
            Logger.getLogger(TCPThread.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(TCPThread.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                oos.close();
                ois.close();
            } catch (IOException ex) {
                Logger.getLogger(TCPThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        try {
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(TCPThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
