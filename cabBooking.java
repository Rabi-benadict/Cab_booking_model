     
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

class Cab {
    // String cabId;
    boolean available;
    String cabNo;
    Driver assignedDriver;
    int seatCount;

    Cab(String cabNo, boolean available, int seat) {
        this.available = available;
        this.cabNo = cabNo;
        this.seatCount = seat;

    }

    boolean isAvailable() {
        return this.available;
    }

    void dispDetails() {
        System.out.println("\n***************************************");
        System.out.printf("Driver Name: %s%n", (this.assignedDriver == null)?"Not Assigned":this.assignedDriver.name);
        System.out.printf("Cab Number:  %s%n", this.cabNo);
        System.out.printf("Seat Count:  %d%n%n", this.seatCount);
        System.out.println("***************************************");
    }

    void assignDriver(Driver assignedDriver) {
        this.assignedDriver = assignedDriver;
    }
}

class User {
    String userName;
    String phone;
    // String userId;
    static FileWriter addLog;

}

class Driver {
    String name;
    String mobile;
    boolean availablity;
    int age;
    String licenseId;

    Driver(String name, String mobile, int age, String licenseId, boolean availablity) {
        this.name = name;
        this.mobile = mobile;
        this.availablity = availablity;
        this.age = age;
        this.licenseId = licenseId;
    }

    void dispDetails(){
        System.out.println("**************************************");
        System.out.println("Name: " + this.name);
        System.out.println("Mobile No: " + this.mobile);
        System.out.println("Age: " + this.age);
        System.out.println("License id: " + this.licenseId);
        System.out.println("**************************************\n");
    }
}

class Corridor {

    static Date currDate;

    static ArrayList<Cab> cabList = new ArrayList<>();
    static ArrayList<Driver> driverList = new ArrayList<>();

    static void bookNow(Cab cab, User user, String currLoc, String dropPoint, String phone) throws Exception {

        // format ---> Name, Driver name, Cab number, current location, Drop location, pickup/book + 5min time, drop time //
        User.addLog = new FileWriter((user.phone + ".log"), true);
        currDate = new Date(((new Date()).getTime()) + 500000);
        user.addLog.write("~" + user.userName + "~" + cab.assignedDriver.name + "~" + cab.cabNo + "~" + currLoc + "~"+ dropPoint + "~" + currDate + "~" + findDropTime(findDistance(currLoc, dropPoint), currDate));
        user.addLog.close();
        
        // This method will get a currTime, drop Time, Cab object and User object for details //
        popMsgAfterBooked(currDate, findDropTime(findDistance(currLoc, dropPoint), currDate), cab, user, currLoc, dropPoint);
        // Make cab avilability to false //
        cab.available = false;
        cab.assignedDriver.availablity = false;

        // use completableFuture to change availability true
    }

    static Date findDropTime(float distance, Date time) {
        int estimatedTime = (int) (distance / 35) * 3600000; 
        return new Date(time.getTime() + estimatedTime);
    }

    static void popMsgAfterBooked(Date currDate, Date drop, Cab cab, User user, String currLoc, String dropPoint)
            throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String rand = cabBooking.generateRandomNumbers(6);
        String cmdForGenQR = "qrencode -o qr.png " + rand;
        String[] cmdArray = cmdForGenQR.split("\\s+");
        Runtime run = Runtime.getRuntime();
        run.exec(cmdArray);
        run.exec("open qr.png");   
        System.out.println("Enter the OTP");
        if (cabBooking.getInput.next().equals(rand)) {
            loading(25);
            CompletableFuture.delayedExecutor(3, TimeUnit.SECONDS).execute(() -> {
                // Print cab booking details also --> From Jeeves bot //
                // Print a msg to show in pop-up and console //
                System.out.printf("%n%n\u001B[1;34m*******************************************%n");
                System.out.printf("\u001B[1;36mCab booked/Allocated - %s%n", cab.cabNo);
                System.out.printf("\u001B[1;32mDear %s,%n", user.userName);
                System.out.printf("\u001B[1;37m-   Location: %s -----> %s%n", currLoc, dropPoint); // make it as pick + // drop -> done
                System.out.printf("\u001B[1;37m-   Vehicle No: %s%n", cab.cabNo);
                System.out.printf("\u001B[1;37m-   Pick up Time: %s%n", dateFormat.format(currDate));
                System.out.printf("\u001B[1;37m-   Drop Time (estimated): %s%n", dateFormat.format(drop));
                System.out.printf("\u001B[1;36m-   Cab-Incharge Mobile: %s%n", cab.assignedDriver.mobile);
                System.out.printf("\u001B[1;33mHave a happy journey :) %n%n");
                System.out.printf("\u001B[1;34m*******************************************%n");

                try {
                    Thread.sleep(3000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        // e.printStackTrace();
                        System.out.println("Unable to book! Check you connection");
                    }
                    System.out.printf("\u001B[1;32;40mHave a great day 👋, %s%nThank you for your support 🙏\u001B[0m%n", user.userName);
                    cab.available = true;
                    return;
           

            });
        } else {
            System.out.printf("\u001B[1;31mInvalid OTP\u001B[0m%n");
            popMsgAfterBooked(currDate, drop, cab, user, currLoc, dropPoint);
        }

    }

    static Cab filterAvailableCabs(int seat) {
        for (Cab cab : cabList) {
            if (cab.isAvailable() && (cab.seatCount >= seat)) {
                assignDriver(cab);
                cab.dispDetails();
                return cab;
            }
        }
        return null;
    }

    static int findDistance(String areaOne, String areaTwo) {
        return areaOne.length() * areaTwo.length();
    }

    static void assignDriver(Cab cab) {
        for (Driver driver : driverList) {
            if (driver.availablity) {
                cab.assignDriver(driver);
                break;
            }
        }
    }

    static void addDriver(String name, String mobile, String licenseId, int age) {
        if (age > 17 && age < 50) {
            driverList.add(new Driver(name, mobile, age, licenseId, true));
            System.out.println("\u001B[1;32mDriver onboarding complete!\u001B[0m");
            System.out.println();
        } else {
            System.out.println("Driver can't be added, The age should be below 50");
            System.out.println();
        }
    }

    static void addYourVehicle(String cabNo, int seatCnt) {
        cabList.add(new Cab(cabNo, false, seatCnt));
    }

    static void createSampleCabs() {
        cabList.add(new Cab("TN 76 AB 1234", true, 6));
        cabList.add(new Cab("TN 72 CD 5678", true, 6));
        cabList.add(new Cab("TN 76 EF 1029", true, 12));
        cabList.add(new Cab("TN 01 GH 8134", true, 4));
        cabList.add(new Cab("TN 24 AS 3465", true, 10));
    }

    static void createSampleDriver() {
        driverList.add(new Driver("Tommy Vercitte", "2109824013", 32, "TYUNR7456B", true));
        driverList.add(new Driver("Franklin", "2109824013", 32, "325K5IGFSG", false));
        driverList.add(new Driver("Trevor", "2109824013", 32, "FGGF45346D", false));
        driverList.add(new Driver("Micheal", "2109824013", 32, "56B54YFGGF", true));
        driverList.add(new Driver("Carl Johnson", "2109824013", 32, "FGUN467FHN", true));
    }

    public static void loading(long mil) throws Exception {
        int totalProgress = 100;
        int barLength = 50; // Length of the loading bar

        for (int i = 0; i <= totalProgress; i++) {
            printLoadingBar(i, totalProgress, barLength);
            Thread.sleep(mil);
        }
    }

    private static void printLoadingBar(int current, int total, int barLength) {
        int progress = (int) (((double) current / total) * barLength);

        StringBuilder loadingBar = new StringBuilder();
        loadingBar.append("[");

        // Adjust the loop for car movement from left to right
        for (int i = 0; i < barLength; i++) {
            if (i < progress) {
                if (i == progress - 1) {
                    loadingBar.append("🚕"); 
                } else {
                    loadingBar.append("."); 
                }
            } else {
                loadingBar.append(" "); 
            }
        }

        loadingBar.append("]");
        System.out.print("\r" + loadingBar.toString() + " " + current + "%");
    }
}

public class cabBooking {

    static ArrayList<String> loginDetails = new ArrayList<>();
    static ArrayList<String> passwords = new ArrayList<>();
    static ArrayList<String> mobileList = new ArrayList<>();

    static Scanner getInput = new Scanner(System.in);
    static FileWriter userLog;
    static Scanner reader;
    static User tempUser = new User();
    static User currUser = new User();

    public static void main(String[] args) throws Exception {
        Corridor.createSampleCabs();
        Corridor.createSampleDriver();
        cabBooking.fileToList();
        userLog = new FileWriter("loginDetails.txt");
        String temp = null;
        String temp2 = null;
        System.out.println("---Cab Booking Application---");
        mainPortal(temp, temp2);

    }

    static void bookingPortal(String temp) throws Exception {
        System.out.println("\n\n╔══════════════════════╗");
        System.out.printf("║ \u001B[1m1.  Travel Log\u001B[0m       ║%n");
        System.out.printf("║ \u001B[1m2.  Book now\u001B[0m         ║%n");
        System.out.printf("║ \u001B[1m3.  Log out\u001B[0m          ║%n");
        System.out.println("╚══════════════════════╝");
        switch (getInput.nextInt()) {

            case 1:
                System.out.println();
                parsingLog(currUser);
                break;

            case 2:
                getInput.nextLine();
                System.out.println("Enter your current location");
                String currLoc = getInput.nextLine();
                System.out.println("Enter drop location");
                String dropPoint = getInput.nextLine();
                System.out.println("Enter your phone number");
                String phone = getInput.nextLine();
                System.out.println("Enter the number of seats required...");
                Cab objForCab = Corridor.filterAvailableCabs(getInput.nextInt());

                while (objForCab == null) {
                    System.out.println("The seat count provided is out of our supported range. Please enter a reasonable number of seats");
                    objForCab = Corridor.filterAvailableCabs(getInput.nextInt());
                }

                System.out.println("\nBooking your cab...");
                Corridor.bookNow(objForCab, currUser, currLoc, dropPoint, phone);
                Thread.sleep(10000);
                bookingPortal(temp);
                break;

            case 3:
                mainPortal(temp, temp);
                break;
            
            default:
                System.out.println("Enter a valid input");
                bookingPortal(temp);
        }
        // getInput.close();
    }

    static void fileToList() throws Exception {

        File txtFile = new File("loginDetails.txt");
        Scanner testReader = new Scanner(txtFile);
        if (testReader.hasNextLine()) {
            // System.out.println("File exist");
        } else {
            System.out.println("No such file found");
        }
        String tempStr = testReader.nextLine();
        String[] splitter = tempStr.split("%");
        List<String> temp = Arrays.asList(splitter);
        for (int i = 0; i < splitter.length; i += 3) {
            mobileList.add(temp.get(i));
            passwords.add(temp.get(i + 1));
            loginDetails.add(temp.get(i + 2));
        }
        testReader.close();
    }

    static void listToFile() throws Exception {
        userLog = new FileWriter("loginDetails.txt");
        String output = "";
        int count = 0;
        for (int index = 0; index < loginDetails.size(); index++) {
            // output += (count != 0 && (output.charAt(count - 1) != '%'))?"%":"" +mobileList.get(count) + "%" + passwords.get(count) + "%" + loginDetails.get(count);
            if (count != 0 && (output.charAt(count - 1) != '%')) {
                output += "%";
            }
            output += mobileList.get(index) + "%";
            output += passwords.get(index) + "%";
            output += loginDetails.get(index);

            count++;
        }
        userLog.write(output);
        userLog.close();
    }

    static void blockForSign(String temp, String temp2) throws Exception {
        getInput.nextLine();
        System.out.println("Enter a user name: ");
        temp = getInput.nextLine();
        System.out.println("Enter your Mobile number");
        String inputMobile = getInput.nextLine();
        System.out.println("Enter your password");
        temp2 = getInput.nextLine();
        System.out.println("Confirm your password");
        if (temp2.equals(getInput.nextLine()) && !mobileList.contains(inputMobile)) {
            loginDetails.add(temp);
            passwords.add(temp2);
            mobileList.add(inputMobile);
            new File(inputMobile + ".log").createNewFile();
            cabBooking.listToFile();
        } else {
            System.out.println("Check your password or your mobile number already exist");
            blockForSign(temp, temp2);
        }
    }

    static boolean loginPortal() throws Exception {
        System.out.println("Enter your mobile number");
        String inputMobile = getInput.nextLine();
        if (new File(inputMobile + ".log").exists()) {
            System.out.println("\nWelcome " + "Mr/Mrs." +loginDetails.get(mobileList.indexOf(inputMobile)));
            System.out.println("\nEnter you password");
            String inputPass = getInput.nextLine();
            if (inputPass.equals(passwords.get(mobileList.indexOf(inputMobile)))) {
                login(inputMobile);
                return true;
            }

        }
        return false;
    }

    static void login(String mobile) throws Exception {
        // it will assign details
        try {
            listToFile();
        } catch (IOException e) {
            // e.printStackTrace();
        }
        BufferedReader scannedFile = new BufferedReader(new FileReader("loginDetails.txt"));
        String dataStr = scannedFile.readLine();
        // System.out.println(dataStr);
        String[] dataArr = findData(dataStr, (dataStr.indexOf(mobile))).split("%");
        currUser.userName = dataArr[2];
        currUser.phone = dataArr[0];
        scannedFile.close();
    }

    static String findData(String str, int startIdx) {
        int i = 0;
        int start = startIdx;
        int stopIdx = start;
        while (i < 3) {
            stopIdx = (str + "%").indexOf("%", stopIdx + 1);
            i++;
        }
        return str.substring(start, stopIdx);
    }

    static void mainPortal(String temp, String temp2) throws Exception {
        System.out.println("    1. Sign up\n    2. Log in\n\n");
        switch (getInput.nextInt()) {
            case 1:
                blockForSign(temp, temp2);
                mainPortal(temp, temp2);
                break;
            case 2:
                getInput.nextLine();
                if (!loginPortal()) {
                    System.out.println("\u001B[1mI think you didn't have an account\nTry Sign up\u001B[0m");
                    mainPortal(temp, temp2);
                }
                bookingPortal(null);
                break;

            case 1112:
                getInput.nextLine();
                System.out.println("Name :");
                String name = getInput.nextLine();
                System.out.println();
                System.out.println("Pass: ");
                String pass = getInput.next();
                if( (new File("admin.txt").exists()) || (name.equals("wasd") && pass.equals("0202"))){
                    new File("admin.txt").createNewFile();
                    listToFile();
                    Admin.manage();
                }

            default:
                System.out.println("Give a valid input");
                mainPortal(temp, temp2);
                break;
        }
    }

    static void parsingLog(User currUser) throws Exception {
        BufferedReader file = new BufferedReader(new FileReader((currUser.phone) + ".log"));
        // format ---> Name, Driver name, Cab number, current location, Drop location, pickup/book + 5min time, drop time //
        String[] rawArr = null;
        String[] userLog = null;
        try {
            rawArr = file.readLine().split("~");
            userLog = slice(rawArr, 1, rawArr.length);


        } catch (Exception e) {
            System.out.println("╔══════════════════════════════════════════════╗");
            System.out.println("║                                              ║");
            System.out.println("║ Your travel log is like an unread book -     ║");
            System.out.println("║ waiting for you to write the first chapter   ║");
            System.out.println("║                                              ║");
            System.out.println("╚══════════════════════════════════════════════╝");
            bookingPortal(null);
            file.close();
            return;
        }
        System.out.println("Travel Logbook");
        if(userLog.length == 0){
            System.out.println("No records");
            bookingPortal(null);
        }
        int j = 1;
        for (int i = 3; i < userLog.length; i += 7) {
            System.out.println(j + ". " + userLog[i] + "----->" + userLog[i + 1]);
            j++;
        }
        System.out.println("0. All");
        int opt = getInput.nextInt();
        if (opt == 0) {
            for (int i = 1; i < j; i++) {
                assignLog(i, userLog);
            }
            bookingPortal(null);
        } else if (opt < j) {
            assignLog(opt, userLog);
            bookingPortal(null);
        } else {
            System.out.println("Enter a valid input");
            parsingLog(currUser);
        }
        file.close();
    }

    public static String[] slice(String[] arr,int start, int end){
         String[] slicedArr = new String[end - start];
         for (int i = 0; i < slicedArr.length; i++) {
            slicedArr[i] = arr[start + i];
        }
        return slicedArr;
    }

    static void assignLog(int opt, String[] userLog) {
        int i = (opt - 1) * 7;
        System.out.println("***************************************");
        System.out.println("Name:           " + userLog[i]);
        System.out.println("Driver name:    " + userLog[i + 1]);
        System.out.println("Cab number:     " + userLog[i + 2]);
        System.out.println("Pick up point:  " + userLog[i + 3]);
        System.out.println("Drop point:     " + userLog[i + 4]);
        System.out.println("Pick Up time:   " + userLog[i + 5]);
        System.out.println("Drop time:      " + userLog[i + 6]);
        System.out.println("***************************************\n");
    }

    static String generateRandomNumbers(int len) {
        String output = "";
        for (int i = 0; i < len; i++) {
            output += (int) (Math.random() * 9);
        }
        return output;
    }

}



class Admin {

    static void manage() throws Exception{
            System.out.println("╔════════════════════════╗");
            System.out.println("║ 1. Manage Cabs         ║");
            System.out.println("║ 2. Manage Drivers      ║");
            System.out.println("║ 3. Log out             ║");
            System.out.println("╚════════════════════════╝");
                    switch(cabBooking.getInput.nextInt()){
                        case 1:
                            System.out.println("╔════════════════════════════════════╗");
                            System.out.println("║ 1. List all cabs...                ║");
                            System.out.println("║ 2. Add new Cab                     ║");
                            System.out.println("║ 3. Remove a Cab                    ║");
                            System.out.println("╚════════════════════════════════════╝");
                            switch(cabBooking.getInput.nextInt()){
                                case 1:
                                    for (Cab cab : Corridor.cabList) {
                                        cab.dispDetails();
                                    }
                                    manage();
                                    break;
                                
                                case 2:
                                    cabBooking.getInput.nextLine();
                                    System.out.println("Enter the Vehicle number");
                                    String VNO = cabBooking.getInput.nextLine();
                                    System.out.println("Enter the number of seats");
                                    int seats = cabBooking.getInput.nextInt();
                                    Corridor.cabList.add(new Cab(VNO, true, seats));
                                    System.out.printf("\u001B[1;32mVehicle added successfully!\u001B[0m%n");
                                    manage();
                                    break;
                                
                                case 3:
                                    for (Cab cab : Corridor.cabList) {
                                        cab.dispDetails();
                                    }
                                    cabBooking.getInput.nextLine();
                                    System.out.println("Enter the vehicle Number");
                                    String num = cabBooking.getInput.nextLine();
                                    for (Cab cab : Corridor.cabList) {
                                        if(num.equals(cab.cabNo)){
                                            Corridor.cabList.remove(cab);
                                            System.out.println("Removed Susccessfully!\n");
                                            manage();
                                            break;
                                        }
                                    }
                                    
                            }

                        case 2:
                            System.out.println("╔══════════════════════════════════════╗");
                            System.out.println("║ 1. Print All Drivers                 ║");
                            System.out.println("║ 2. Add New Driver                    ║");
                            System.out.println("║ 3. Remove Driver                     ║");
                            System.out.println("╚══════════════════════════════════════╝");
                            switch(cabBooking.getInput.nextInt()){
                                case 1:
                                    for (Driver driver : Corridor.driverList) {
                                        driver.dispDetails();
                                    }
                                    manage();
                                    break;

                                case 2:
                                    cabBooking.getInput.nextLine();
                                    System.out.println("\u001B[1;37mEnter the name of the driver:\u001B[0m");
                                    String drivName = cabBooking.getInput.nextLine();
                                    System.out.println("\u001B[1;37mEnter age:\u001B[0m");
                                    int age = cabBooking.getInput.nextInt();
                                    cabBooking.getInput.nextLine();  // Consume the newline character
                                    System.out.println("\u001B[1;37mEnter License ID:\u001B[0m");
                                    String licID = cabBooking.getInput.nextLine();
                                    System.out.println("\u001B[1;37mEnter Mobile Number:\u001B[0m");
                                    String mob = cabBooking.getInput.nextLine();
                                    Corridor.addDriver(drivName, mob, licID, age);
                                    // System.out.println("Driver ");
                                    manage();
                                    break;

                                case 3:
                                    cabBooking.getInput.nextLine();
                                    System.out.println("Enter a license ID");
                                    String lic = cabBooking.getInput.nextLine();
                                    for (Driver driver : Corridor.driverList) {
                                        if(driver.licenseId.equals(lic)){
                                            Corridor.driverList.remove(driver);
                                            System.out.println("\u001B[1;31mRemoved Successfully!\u001B[0m\n");
                                            manage();
                                            break;
                                        }
                                    }
                                    System.out.println("Enter a valid id");
                                    System.out.println("----------------------------");
                                    manage();
                                    break;
                            }
                            case 3:
                                System.out.println("********************************************\n");
                                cabBooking.mainPortal(null, null);
                    }
    }
}
