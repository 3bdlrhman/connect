package control;

import com.fazecast.jSerialComm.SerialPort;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.awt.AWTException;
import java.awt.SystemTray;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.time.*;
import sandbox.*;

public class control {

    public static void main ( String[] args) throws InterruptedException, AWTException, MalformedURLException
    {
        //Specify your desired serial port
        SerialPort port = SerialPort.getCommPort("COM2");
        port.openPort();
        System.out.println("port opened: "+port.isOpen());
        Sandbox sand = new Sandbox();
        if (SystemTray.isSupported()) {
                    try{
                        sand.displayTray();
                    }catch(AWTException | MalformedURLException ex){
                        
                    }
                } else {
                    System.err.println("System tray not supported!");
                }
        
        // setting up the port parameters it should match what is set at the receving terminal
        port.setComPortParameters(9600,8,1,0);
        port.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 0, 0);
        port.setRTS();

        // before excuting any code we check if the port is opened
        if (port.isOpen()){
            try {
                //first we read the line index for temperature file
                FileReader lineFr = new FileReader
                (new File("C:\\Users\\Prof.Hamdy F.M\\Documents\\NetBeansProjects\\JavaApplication-1\\lineNumber.txt"));
                
                BufferedReader lineBr = new BufferedReader (lineFr);          
                
                // convert the string to number
                int lineNumber = Integer.parseInt(lineBr.readLine());           

                //then we read the exact value of temperature from temperature file
                FileReader tempFr = new FileReader
                (new File("C:\\Users\\Prof.Hamdy F.M\\Documents\\NetBeansProjects\\JavaApplication-1\\temperature.txt"));
                
                BufferedReader tempBr = new BufferedReader (tempFr);
                
                //declearing a variable to recieve the temp. value into
                String value = "";
                if (lineNumber > 0){     
                    for (int i=0; i < lineNumber - 1 ; i++){ tempBr.readLine(); }
                    // temperature value
                    value = tempBr.readLine();
                } else if (lineNumber == 0){
                    value = tempBr.readLine();
                }
                lineFr.close();lineBr.close();            
                tempFr.close();tempBr.close();
                
                int tempVALUE = Integer.parseInt(value.substring(8, 11));
                
                // save the new index of temperature line number
                FileWriter lineWr = new FileWriter
                (new File("C:\\Users\\Prof.Hamdy F.M\\Documents\\NetBeansProjects\\JavaApplication-1\\lineNumber.txt"));        
                BufferedWriter lineBw = new BufferedWriter(lineWr);        
                int w = lineNumber+1;                
                lineBw.write(""+w);        
                lineBw.flush();lineWr.close();lineBw.close();

                //writting to the port
                OutputStream out = port.getOutputStream();
                port.clearRTS();
                String message = value;
                System.out.println(message);
                byte[] b = message.getBytes();
                out.write(b);
                out.flush();

                //reading from the port
                FileReader commFr = new FileReader
                (new File("C:\\Users\\Prof.Hamdy F.M\\Documents\\NetBeansProjects\\JavaApplication-1\\readComm.txt"));
                BufferedReader commBr = new BufferedReader (commFr);
                String s = commBr.readLine();
                byte[] b2 = s.getBytes();
            
                
                InputStream in = port.getInputStream();
                
                 FileWriter infoWr = new FileWriter
                (new File("C:\\Users\\Prof.Hamdy F.M\\Documents\\NetBeansProjects\\JavaApplication-1\\info.txt"), true);        
                BufferedWriter infoBw = new BufferedWriter(infoWr);        
                 
                LocalDate date = LocalDate.now();
                LocalTime time = LocalTime.now();
               
                int c=0;
                port.closePort();
                while (c < 5){
                    Thread.sleep(60000);
                    port.openPort();
                    String back="";
                    for (int i=0; i<24; i++){
                        out.write(b2);
                        back = back + in.read();
                    }
                    String number = back.substring(17);
                    System.out.println("back :  "+back+"\n");                                   
                    int n1 = Integer.parseInt(number.substring(0, 2));
                    int n2 = Integer.parseInt(number.substring(2, 4));
                    int n3 = Integer.parseInt(number.substring(4, 6));
                    int n4 = Integer.parseInt(number.substring(6, 8));
                    int num = Integer.parseInt(String.valueOf(Character.toChars(n1))+String.valueOf(Character.toChars(n2))+String.valueOf(Character.toChars(n3))+String.valueOf(Character.toChars(n4)));
                    int num2 = Integer.parseInt(String.valueOf(Character.toChars(n1))+String.valueOf(Character.toChars(n2)));

                    Double d = new Double(num*0.01);
                    System.out.println("value of tenperature   "+d+" \n");
                    port.closePort();
                
                if(num2 == tempVALUE || num2 == tempVALUE+1 || num2 == tempVALUE-1){
                    c++; 
                    System.out.println("collision");
                }
                
                infoBw.write("-Date: ("+date +")"+"  -Time: ("+time+")"+" -Set Temperature: ("+tempVALUE+")"+" -Current Value ("+num+")");
                infoBw.newLine();
                infoBw.flush();

                }
                
                in.close();
                out.close();
                infoBw.close();
                System.out.println("close the port " + port.closePort());
                
            } catch (FileNotFoundException ex) {System.out.println(ex);}  
              catch (IOException ex) {System.out.println(ex);}
        } 
        else{  
            System.out.println("port is not opened, make sure it's available and try again");
            System.exit(0);
        }
    }

    //takes a number in the format of a string and returns its Hexa value
    public static String convertToHexa(String value){
        int initial =0;
        int[] arr = new int[value.length()];
        for(int i=1 ; i<arr.length-2 ; i++){
            arr[i] = value.charAt(i);
            initial +=arr[i];
        }
        int result = initial % 256;
        String finalResult = Integer.toHexString(result);
        return finalResult;
    }
}