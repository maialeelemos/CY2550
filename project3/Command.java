import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.stream.Stream;

public class Command {
    private final Readable in;
    private final Appendable out;
    private int words = 4;
    // boolean waitingForWords = false;
    private int caps = 0;
    private int nums = 0;
    private int symbols = 0;
    private boolean help = false;

    public Command(Readable in) throws IllegalArgumentException {
        if (in == null) {
            throw new IllegalArgumentException("Input cannot be null");
        } else {
            this.in = in;
            this.out = System.out;
        }
    }   

    public void startProcessing() throws IllegalStateException {
        Scanner sc = new Scanner(this.in);
        // System.out.println("Got to processing.");
        
        while (sc.hasNext()) {
            String userInstruction = sc.next();
            // System.out.println(userInstruction);
      
            // if (waitingForWords) {
            //     words = Integer.valueOf(sc.next()); // this could fail if not an integer
            //     waitingForWords = false;
            // } else 
            if (userInstruction.equals("-h") || userInstruction.equals("--help")) {
                printHelpMessage();
                help = true;
            } else if (userInstruction.equals("-w") || userInstruction.equals("--words")) {
                // waitingForWords = true;
                words = Integer.valueOf(sc.next());
            } else if (userInstruction.equals("-c") || userInstruction.equals("--caps")) {
                caps = Integer.valueOf(sc.next());
            } else if (userInstruction.equals("-n") || userInstruction.equals("--numbers")) {
                nums = Integer.valueOf(sc.next());
            } else if (userInstruction.equals("-s") || userInstruction.equals("--symbols")) {
                symbols = Integer.valueOf(sc.next());
            }
        }

        sc.close();
        // System.out.println("End of processing.");

        if (caps > words) {
            printMessage("Cannot have more capital letters than words. Please try again.");
        } else if (help) {
            // do nothing since message was already printed.
        } else {
            generatePassword();
        }
    }

    public void startProcessingArgs(String[] args) throws IllegalStateException {
        // System.out.println("Got to processing ARGS.");
        
        int i = 0;
        while (i < args.length) {
            // System.out.println(args[i]);
      
            if (args[i].equals("-h") || args[i].equals("--help")) {
                printHelpMessage();
                help = true;
            } else if (args[i].equals("-w") || args[i].equals("--words")) {
                words = Integer.valueOf(args[i+1]);
                i++;
            } else if (args[i].equals("-c") || args[i].equals("--caps")) {
                caps = Integer.valueOf(args[i+1]);
                i++;
            } else if (args[i].equals("-n") || args[i].equals("--numbers")) {
                nums = Integer.valueOf(args[i+1]);
                i++;
            } else if (args[i].equals("-s") || args[i].equals("--symbols")) {
                symbols = Integer.valueOf(args[i+1]);
                i++;
            }
            i++;
        }
        
        // System.out.println("End of processing.");

        if (caps > words) {
            printMessage("Cannot have more capital letters than words. Please try again.");
        } else if (help) {
            // do nothing since message was already printed.
        } else {
            generatePassword();
        }
    }

    public void generatePassword() {
        // generate password with the fields
        String password = "";

            // 370105 is the words.txt length
            while (words>0) {
                // cycle until all words, caps, symbols, ... are used

                int wordPos = generateRandomNumber(0, 370105);

                try (Stream<String> lines = Files.lines(Paths.get("words.txt"))) {
                    String tempPassword = lines.skip(wordPos).findFirst().get();
                    

                    // CAPS
                    tempPassword = addCap(tempPassword);
                    
                    // SYMBOLS
                    tempPassword = addRandomSymbol(tempPassword, true);

                    // NUMBERS
                    tempPassword = addRandomNumber(tempPassword, true);

                    password = password + tempPassword;
                    words--;

                } catch(IOException e){
                    printMessage("Could not read words.txt file");
                    // printMessage(e);
                }
            }

            // Todo: if there are any caps, nums, or symbols left, add them to the end or front right now
            for (int i = caps; i > 0; i--) {
                password = addCap(password);
            }
            for (int i = symbols; i > 0; i--) {
                password = addRandomSymbol(password, false);
            }
            for (int i = nums; i > 0; i--) {
                password = addRandomNumber(password, false);
            }

        printMessage(password);
    }

    public int generateRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    private String addRandomNumber(String str, boolean fullyRandom) {
        String result = str;
        if (nums > 0) {

            // 0 == none, 1 == before, 2 == at end
            int addNum;
            if (fullyRandom) {
                addNum = generateRandomNumber(0, 2);
            } else {
                addNum = generateRandomNumber(1,2);
            }

            if (addNum != 0) {
                int num = generateRandomNumber(0, 9);
                if (addNum == 1) {
                    result = Integer.toString(num) + result;
                } else {
                    result = result + Integer.toString(num);
                }
                nums--;
            }
        }
        return result;
    }

    private String addRandomSymbol(String str, boolean fullyRandom) {
        String result = str;
        if (symbols > 0) {

            // 0 == none, 1 == before, 2 == at end
            int addSymbol;
            if (fullyRandom) {
                addSymbol = generateRandomNumber(0, 2);
            } else {
                addSymbol = generateRandomNumber(1, 2);
            }

            if (addSymbol != 0) {
                // length of symbols.txt = 12
                int symbolI = generateRandomNumber(0, 12);
                String symbolC;
                try {
                    symbolC = Files.readAllLines(Paths.get("symbols.txt")).get(symbolI);
                    if (addSymbol == 1) {
                        result = symbolC + result;
                    } else {
                        result = result + symbolC;
                    }
                    symbols--;
                } catch (IOException e) {
                    printMessage("Could not read symbols.txt file. Please try again");
                } // there could be an error with above try-catch
            }
        }
        return result;
    }

    private String addCap(String str) {
        String result = str;
        if (caps > 0) {
            // 0 == no, 1 == yes add cap
            int addCaps = generateRandomNumber(0, 1);
            if (addCaps != 0 || caps <= words) {
                result = result.substring(0, 1).toUpperCase() + result.substring(1);
                caps--;
            }
        }
        return result;
    }

    private void printHelpMessage() {
        try {
            this.out.append("\n"
                + "usage: xkcdpwgen [-h] [-w WORDS] [-c CAPS] [-n NUMBERS] [-s SYMBOLS]" + "\n"
                + "Generate a secure, memorable password using the XKCD method optional arguments:" + "\n"
                + "-h, --help            show this help message and exit" + "\n"
                + "-w WORDS, --words WORDS            include WORDS words in the password (default=4)" + "\n"
                + "-c CAPS, --caps CAPS            capitalize the first letter of CAPS random words (default=0)" + "\n"
                + "-n NUMBERS, --numbers NUMBERS            insert NUMBERS random numbers in the password (default=0)" + "\n"
                + "-s SYMBOLS, --symbols SYMBOLS            insert SYMBOLS random symbols in the password (default=0)");
        } catch (IOException e) {
            throw new IllegalStateException("Could not print help message.");
        }

    }

    private void printMessage(String str) {
        try {
          this.out.append(str + "\n");
        } catch (IOException e) {
          throw new IllegalStateException("Could not print message.");
        }
      }
}
