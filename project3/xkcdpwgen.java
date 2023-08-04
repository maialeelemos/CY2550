import java.io.InputStreamReader;

// Questions:
// - how to call my functionality from ther terminal?
// - how to do the makefile and xkcdpwgen file ???
// - is it fine that have to call the class again with arguments each time for it to work?
//   my code does not wait for input, it needs it at ocmplie time

public class xkcdpwgen {
    public static void main(String[] args) {
        
        if (args.length == 0) {
            // generate password with four words
            Command c = new Command(new InputStreamReader(System.in));
            c.generatePassword();
        } else if (args.length > 0) {
            // parse input to generate password
            Command c = new Command(new InputStreamReader(System.in));
            c.startProcessingArgs(args);
        } else {
            throw new IllegalArgumentException("Program does not support the given input.");
        }

    }

    
}
