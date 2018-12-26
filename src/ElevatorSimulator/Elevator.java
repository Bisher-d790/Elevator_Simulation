package ElevatorSimulator;

import java.io.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

public class Elevator {

    private ElevatorQueue requests = new ElevatorQueue();
    private LocalTime beginTime;
    private Controller controller;
    private int capacity;
    private int currentFloor;
    private int buildingCapacity;
    private int elevatorSpeed;
    private int spaceBetweenLevels;
    private int doorTime;
    private int direction;
    private int avgPassengersTime;
    private int requestsIDs = 0;
    private Elevate elevateThread = null;
    private ArrayList<String> report;
    private String analysis;
    private int passengersServed = 0;
    private int floorsTravelled = 0;


    public Elevator(Controller controller) {
        //Initializing values
        this.beginTime = LocalTime.now();
        //Setting the controller reference
        this.controller = controller;
        //Starting timer
        controller.startTimer();
        report = new ArrayList<>();
        report.add("Request ID,Source,Destination,Number Of People,Status,Request Time");
        analysis = "Num Of Requests,Passengers Served,Floors Travelled,Time Taken";
        setCapacity(10);
        setCurrentFloor(0);
        buildingCapacity = 11;
        elevatorSpeed = 3;
        spaceBetweenLevels = 3;
        setDirection(0);
        doorTime = 5;
        avgPassengersTime = 4;
        controller.setOperationText("Idle");
        startElevator();


        //Writing to Log file
        LogWrite("\n\nLog File Elevator:\nBegin Time set:\t" + beginTime
                + "\nCapacity Set: " + capacity
                + "\nFloor Set: " + currentFloor
                + "\nBuilding Capacity Set: " + buildingCapacity
                + "\nElevator Speed Set: " + elevatorSpeed
                + "\nSpace Between Floors Set: " + spaceBetweenLevels
                + "\nDoor Open\\Close Time Set: " + doorTime
                + "\nAvg Passanger Time Set: " + avgPassengersTime
                + "\n");
    }


    public void addRequest(int src, int dest, int nOfPeople) {

        report.add(requestsIDs + ",");
        report.set(report.size() - 1, report.get(report.size() - 1) + src + ",");
        report.set(report.size() - 1, report.get(report.size() - 1) + dest + ",");
        report.set(report.size() - 1, report.get(report.size() - 1) + nOfPeople + ",");


        //Red light blink
        if (dest > getBuildingCapacity() || src > getBuildingCapacity() || dest < 0 || src < 0) {
            report.set(report.size() - 1, report.get(report.size() - 1) + "Rejected" + ",");
            LogWrite(LocalTime.now() + "\t at " + controller.getTime() + "\tRequest Rejected:\t Source or Destination out of bounds!");
            controller.setRequestStatus(-1);
            return;
        }

        report.set(report.size() - 1, report.get(report.size() - 1) + "Accepted" + ",");
        passengersServed += nOfPeople;
        //Adding a request to the requests queue
        requests.enqueue(new request(src, dest, nOfPeople));

        LogWrite(LocalTime.now() + "\t at " + controller.getTime() + "\tRequest with ID " + (requestsIDs - 1) + " : Source  (" + src + "), Destination  (" + dest + "), #OfPassangers (" + nOfPeople + ")\tadded");

        //Green light blink
        controller.setRequestStatus(1);

    }

    public void sortRequests() {
        requests.sort(true);
        elevateThread.pendingRequests.sort(false);
    }

    public void startElevator() {
        //Starts the elevator
        //Keeps cycling till a new request is added
        Thread start = new Thread(() -> {
            while (true) {
                if (!requests.isEmpty()) {
                    //if a request is added, and new Elevate thread is  created
                    elevateThread = new Elevate();
                    elevateThread.start();
                    LogWrite(LocalTime.now() + "\t at " + controller.getTime() + "\tElevator Initiated...");
                    System.out.println("Elevator Started !");
                    break;
                }
                System.out.println("Searching...");
            }
        });
        start.start();
    }

    public LocalTime getBeginTime() {
        return beginTime;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        //Setting the capacity to local var and front end text
        this.capacity = capacity;
        controller.setCapacityText(capacity);
    }

    public int getCurrentFloor() {
        return currentFloor;
    }

    public void setCurrentFloor(int currentFloor) {
        //Setting the current floor to local var and front end text
        this.currentFloor = currentFloor;
        controller.setLevelText(currentFloor);
    }

    public int getBuildingCapacity() {
        return buildingCapacity;
    }

    public void setBuildingCapacity(int buildingCapacity) {
        this.buildingCapacity = buildingCapacity;
    }

    public int getElevatorSpeed() {
        return elevatorSpeed;
    }

    public void setElevatorSpeed(int elevatorSpeed) {
        this.elevatorSpeed = elevatorSpeed;
    }

    public int getSpaceBetweenLevels() {
        return spaceBetweenLevels;
    }

    public void setSpaceBetweenLevels(int spaceBetweenLevels) {
        this.spaceBetweenLevels = spaceBetweenLevels;
    }

    public int getDoorTime() {
        return doorTime;
    }

    public void setDoorTime(int doorTime) {
        this.doorTime = doorTime;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        //Setting the direction to local var and front end arrow indicators
        if (direction > 0) LogWrite(LocalTime.now() + "\t at " + controller.getTime() + "\tElevator Acsending");
        if (direction < 0) LogWrite(LocalTime.now() + "\t at " + controller.getTime() + "\tElevator Descending");
        if (direction == 0) LogWrite(LocalTime.now() + "\t at " + controller.getTime() + "\tElevator Idle");
        this.direction = direction;
        controller.setArrowImg(direction);
    }

    public int getAvgPassengersTime() {
        return avgPassengersTime;
    }

    public void setAvgPassengersTime(int avgPassengersTime) {
        this.avgPassengersTime = avgPassengersTime;
    }

    public boolean isEmpty() {
        return requests.isEmpty();
    }

    public boolean isFull() {
        return requests.getSize() >= capacity;
    }

    public void GenerateReport() {

        if (report.isEmpty()) return;

        analysis += requestsIDs + ",";
        requestsIDs = 0;
        analysis += passengersServed + ",";
        passengersServed = 0;
        analysis += floorsTravelled + ",";
        floorsTravelled = 0;
        analysis += controller.getTime() + ",";

        try {
            PrintWriter a = new PrintWriter(new FileOutputStream("Report.csv", true));
            a.println(analysis);
            while (!report.isEmpty()) {
                a.println(report.remove(0));
            }
            report.clear();
            analysis = "";
            a.close();
        } catch (IOException e) {
        }
    }


    public void LogWrite(String line) {
        try {
            PrintWriter a = new PrintWriter(new FileOutputStream("Log.log", true));
            a.println(line);
            a.close();
        } catch (IOException e) {
        }
    }


    private void elevateOne(boolean doorStart, boolean doorEnd, boolean direction) throws InterruptedException {
        if (doorStart) doorOpenClose(true);

        if (direction) {
            controller.setOperationText("Going Up");
            Thread.sleep((spaceBetweenLevels / elevatorSpeed) * 1000);
            setCurrentFloor(getCurrentFloor() + 1);
        } else {

            controller.setOperationText("Going down");
            Thread.sleep((spaceBetweenLevels / elevatorSpeed) * 1000);
            setCurrentFloor(getCurrentFloor() - 1);
        }

        if (doorEnd) doorOpenClose(false);

        floorsTravelled++;
    }

    private void doorOpenClose(boolean enterOrExit) throws InterruptedException {
        controller.setOperationText("Opening Door");
        LogWrite(LocalTime.now() + "\t at " + controller.getTime() + "\tOpening Door");
        Thread.sleep(doorTime * 1000);

        if (enterOrExit) {
            controller.setOperationText("Passengers entering");
            LogWrite(LocalTime.now() + "\t at " + controller.getTime() + "\tPassengers Entering");
        } else {
            controller.setOperationText("Passengers exiting");
            LogWrite(LocalTime.now() + "\t at " + controller.getTime() + "\tPassengers Exiting");
        }
        Thread.sleep(avgPassengersTime * 1000);

        controller.setOperationText("Closing Door");
        LogWrite(LocalTime.now() + "\t at " + controller.getTime() + "\tClosing Door");
        Thread.sleep(doorTime * 1000);
    }

    private class Elevate extends Thread {
        int requestsServed = 0;
        request bufferRequest;
        // A queue for requests already received
        private ElevatorQueue pendingRequests = new ElevatorQueue();

        public Elevate() {
        }

        @Override
        public void run() {
            while (!requests.isEmpty()) {
                //add the first request to pursue
                bufferRequest = requests.dequeue();
                LogWrite(LocalTime.now() + "\t at " + controller.getTime() + "\tStart Moving");
                startElevate(bufferRequest.src, bufferRequest.dest, bufferRequest.nOfPeople);
                //Finish the serviced requests before taking any new ones
                while (!pendingRequests.isEmpty()) {
                    bufferRequest = pendingRequests.dequeue();
                    startElevate(bufferRequest.src, bufferRequest.dest, bufferRequest.nOfPeople);
                }
            }
            requestsServed = 0;
            //Returns to level 0 after operation
            if (currentFloor != 0) addRequest(currentFloor, 0, 0);
            controller.setOperationText("Idle");
            setDirection(0);
            LogWrite(LocalTime.now() + "\t at " + controller.getTime() + "\tFinished Requests...");
            startElevator();
        }

        private void startElevate(int src, int dest, int nOfPeople) {
            try {
                if (nOfPeople == 0) {
                    if (dest - src < 0) {
                        setDirection(-1);
                        while (currentFloor > dest) {
                            elevateOne(false, false, false);
                            checkInterrupt();
                        }
                    } else {
                        setDirection(1);
                        while (currentFloor < dest) {
                            elevateOne(false, false, true);
                            checkInterrupt();
                        }
                    }
                    return;
                }
                if (currentFloor != src) {
                    if (currentFloor < src) {
                        setDirection(1);
                        if (src - currentFloor == 1) {
                            elevateOne(false, false, true);
                        } else {
                            while (currentFloor < src - 1) {
                                elevateOne(false, false, true);
                                checkInterrupt();
                            }
                            if (currentFloor != src) elevateOne(false, false, true);
                        }
                        checkInterrupt();
                    } else {
                        setDirection(-1);
                        if (currentFloor - src == 1) {
                            elevateOne(false, false, false);
                        } else {
                            while (currentFloor > src + 1) {
                                elevateOne(false, false, false);
                                checkInterrupt();
                            }
                            if (currentFloor != src) elevateOne(false, false, false);
                        }
                        checkInterrupt();
                    }
                }

                doorOpenClose(true);
                setCapacity(getCapacity() - bufferRequest.nOfPeople);


                if (currentFloor != dest) {
                    if (currentFloor < dest) {
                        setDirection(1);
                        if (dest - currentFloor == 1) {
                            elevateOne(false, true, true);
                            setCapacity(getCapacity() + bufferRequest.nOfPeople);
                        } else {
                            while (currentFloor < dest - 1) {
                                elevateOne(false, false, true);
                                checkInterrupt();
                            }
                            if (currentFloor != dest)
                                elevateOne(false, true, true);
                            setCapacity(getCapacity() + bufferRequest.nOfPeople);
                        }
                        checkInterrupt();
                    } else {
                        setDirection(-1);
                        if (currentFloor - dest == 1) {
                            elevateOne(false, true, false);
                            setCapacity(getCapacity() + bufferRequest.nOfPeople);
                        } else {
                            while (currentFloor > dest + 1) {
                                elevateOne(false, false, false);
                                checkInterrupt();
                            }
                            if (currentFloor != dest)
                                elevateOne(false, true, false);
                            setCapacity(getCapacity() + bufferRequest.nOfPeople);
                        }
                        checkInterrupt();
                    }
                }


            } catch (InterruptedException e) {
            }
        }

        private boolean checkInterrupt() throws InterruptedException {

            //Checks if current floor has any requests that can be added to pending,
            // and checks if it has ay pending requests
            sortRequests();

            boolean flag = false;

            if (!requests.isEmpty() && currentFloor == requests.peek().src && (((requests.peek().dest - requests.peek().src) > 0 && direction > 0) || ((requests.peek().dest - requests.peek().src) < 0 && direction < 0))) {
                doorOpenClose(true);
                if (requests.peek().nOfPeople <= getCapacity()) {
                    setCapacity(getCapacity() - requests.peek().nOfPeople);
                    pendingRequests.enqueue(requests.dequeue());
                } else {
                    requests.peek().nOfPeople = requests.peek().nOfPeople - getCapacity();
                    request temp = new request();
                    temp.copy(requests.peek());
                    temp.nOfPeople = getCapacity();
                    pendingRequests.enqueue(temp);
                    setCapacity(0);
                }
                flag = true;
            }

            if (!pendingRequests.isEmpty() && currentFloor == pendingRequests.peek().dest) {
                doorOpenClose(false);
                setCapacity(getCapacity() + pendingRequests.peek().nOfPeople);
                pendingRequests.dequeue();
                flag = true;
            }
            if (flag) checkInterrupt();
            return flag;
        }
    }


    private class request implements Comparable {
        //Defines  a new type var request to adapt the elevator environment
        public int src, dest, nOfPeople, ID;
        public LocalTime requestTime;

        public request(int src, int dest, int nOfPeople) {
            this.dest = dest;
            this.src = src;
            this.nOfPeople = nOfPeople;
            this.ID = requestsIDs++;

            requestTime = LocalTime.now().minusHours(beginTime.getHour());
            requestTime = LocalTime.now().minusMinutes(beginTime.getMinute());
            requestTime = LocalTime.now().minusSeconds(beginTime.getSecond());
            requestTime = LocalTime.now().minusNanos(beginTime.getNano());

            report.set(report.size() - 1, report.get(report.size() - 1) + requestTime + ",");
        }

        public request() {
        }

        public void copy(request other) {
            src = other.src;
            dest = other.dest;
            nOfPeople = other.nOfPeople;
            ID = other.ID;
            requestTime = other.requestTime;
        }

        @Override
        public int compareTo(Object o) {
            return compareTo((int) o, true);
        }

        private int compareTo(int o, boolean srcOrDest) {
            if (srcOrDest) return this.src - o;
            else return this.dest - o;
        }
    }

    private class ElevatorQueue {
        //Defines an elevator queue to adapt the elevator functionality
        private LinkedList<request> queue = new LinkedList<>();


        public ElevatorQueue() {
        }

        public ElevatorQueue(request[] e) {
            for (request e1 : e) {
                queue.addLast(e1);
            }
        }

        public void enqueue(request e) {
            queue.addLast(e);
        }

        public request dequeue() {
            return queue.removeFirst();
        }

        public request getElement(int i) {
            return queue.get(i);
        }

        public request peek() {
            return queue.getFirst();
        }

        public int getSize() {
            return queue.size();
        }

        public boolean contains(request e) {
            return queue.contains(e);
        }

        public boolean isEmpty() {
            return queue.isEmpty();
        }

        @Override
        public String toString() {
            return queue.toString();
        }

        private void sort(boolean srcOrDest) {
            //Sorts the queue accordingly with the current floor,
            // direction and in respect of Source or Destination of requests

            if (queue.isEmpty()) return;

            boolean dir;
            if (direction > 0) dir = true;
            else dir = false;

            quickSort(srcOrDest, 0, getSize(), !dir);

            int i;
            if (dir) for (i = 0; i < getSize() && queue.get(i).compareTo(currentFloor, srcOrDest) > 0; i++) {
                if (queue.size() == 1) {
                    i = 0;
                    break;
                }
            }
            else for (i = 0; i < getSize() && queue.get(i).compareTo(currentFloor, srcOrDest) < 0; i++) {
                if (queue.size() == 1) {
                    i = 0;
                    break;
                }
            }


            quickSort(srcOrDest, 0, i, dir);
            i++;
            quickSort(srcOrDest, i, getSize(), !dir);
        }

        private void quickSort(boolean srcOrDest, int left, int right, boolean asc) {
            int l = left;
            int r = right - 1;
            int size = right - left;
            if (size > 1) {
                Random rn = new Random();
                request pivot = queue.get(rn.nextInt(size) + l);
                if (srcOrDest) while (l < r) {
                    if (asc) {
                        while (queue.get(r).compareTo(pivot.src) > 0 && r > l) {
                            r--;
                        }
                        while (queue.get(l).compareTo(pivot.src) < 0 && l <= r) {
                            l++;
                        }
                    } else {
                        while (queue.get(r).compareTo(pivot.src) < 0 && r > l) {
                            r--;
                        }
                        while (queue.get(l).compareTo(pivot.src) > 0 && l <= r) {
                            l++;
                        }
                    }
                    l = swapQS(l, r);
                }
                else
                    while (l < r) {
                        if (asc) {
                            while (queue.get(r).compareTo(pivot.dest) > 0 && r > l) {
                                r--;
                            }
                            while (queue.get(l).compareTo(pivot.dest) < 0 && l <= r) {
                                l++;
                            }
                        } else {
                            while (queue.get(r).compareTo(pivot.dest) < 0 && r > l) {
                                r--;
                            }
                            while (queue.get(l).compareTo(pivot.dest) > 0 && l <= r) {
                                l++;
                            }
                        }
                        l = swapQS(l, r);
                    }

                quickSort(srcOrDest, left, l, asc);
                quickSort(srcOrDest, r, right, asc);
            }
        }

        private int swapQS(int l, int r) {
            if (l < r) {
                request temp = new request();
                temp.copy(queue.get(l));
                replace(l, queue.get(r));
                replace(r, temp);
                l++;
            }
            return l;
        }

        public void replace(int index, request e) {
            queue.get(index).copy(e);
        }

    }
}
