import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;


public class MenuScanner {
    Scanner keyboardEntry;

    public MenuScanner(){
        this.keyboardEntry = new Scanner(System.in);
    }

    public int readInt() throws InputMismatchException{
        try{
            int option = this.keyboardEntry.nextInt();
            this.keyboardEntry.nextLine();
            return option;
        }catch (InputMismatchException e) {
            return 0;
        }  
    }

    public String readString(){
        String info = this.keyboardEntry.nextLine();
        return info;
    }

    public String readContinue(){
        boolean accepted = false;
        String info= "";
        System.out.println("Display Menu? (y/n)");
        while(!accepted){         
            info = this.keyboardEntry.nextLine();
            if(info.equals("y")){
                accepted = true;
            }else if(info.equals("n")){
                accepted = true;
            }
        }
        
        return info;
    }
}