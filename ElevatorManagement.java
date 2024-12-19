import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;


public class ElevatorManagement {
    private final List<Elevator> elevators = new ArrayList<>();
    private final List<Request> activeRequests = new ArrayList<>();
    private final ElevatorGUI elevatorGUI;
    private final int maxFloor;

    public ElevatorManagement(int numElevators, int maxFloor, ElevatorGUI gui) {
        this.elevatorGUI = gui;
        this.maxFloor = maxFloor;

        for (int i = 0; i < numElevators; i++) {
            Elevator elevator = new Elevator(i);
            elevators.add(elevator);
            new Thread(elevator).start();
        }
    }

    public synchronized void handleRequest(Request request) {
        activeRequests.add(request);
        distributionRequest();
        updateGUI();
    }

    private synchronized void distributionRequest() {
        for (Elevator i : elevators) {
            if (i.checkBusy() && !activeRequests.isEmpty()) {
                Request request = activeRequests.remove(0);
                i.addActiveRequest(request);
            }
        }
        updateGUI();
    }

    private synchronized void updateGUI() {
        elevatorGUI.updateRequests(activeRequests);
    }

    public synchronized void completeRequest(Request request, int elevatorId) {
        elevatorGUI.addCompletedRequest(request, elevatorId);
        updateGUI();
    }

    private class Elevator implements Runnable {
        private final int id;
        private int currentFloor = 0;
        private final BlockingQueue<Request> requests = new LinkedBlockingQueue<>();

        public Elevator(int id) {
            this.id = id;
        }

        public synchronized void addActiveRequest(Request request) {
            requests.add(request);
        }

        public boolean checkBusy() {
            return requests.isEmpty();
        }

        private void shifting(int floor) throws InterruptedException {
            while (currentFloor != floor) {
                if (currentFloor > floor) {
                    currentFloor--;
                } else {
                    currentFloor++;
                }
                elevatorGUI.elevatorInfo(id, currentFloor);
                Thread.sleep(400);
            }
        }
        @Override
        public void run() {
            while (true) {
                try {
                    Request request = requests.poll();
                    if (request != null) {
                        shifting(request.getStartFloor());
                        shifting(request.getFinishFloor());
                        completeRequest(request, id);
                    } else {
                        Thread.sleep(100);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}
