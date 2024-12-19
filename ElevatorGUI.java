import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class ElevatorGUI extends JFrame {
    private List<JLabel> elevatorLabels = new ArrayList<>();
    private ElevatorManagement system;
    private JTextArea activeRequestsWindow = new JTextArea(10, 30);
    private JTextArea completedRequestsWindow = new JTextArea(10, 30);

    public ElevatorGUI(int numElevators, int maxFloor) {
        setTitle("Elevator Controlling");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel elevatorPanel = new JPanel();
        elevatorPanel.setLayout(new GridLayout(numElevators, 1, 10, 10));
        for (int i = 1; i <= numElevators; i++) {
            JLabel label = new JLabel("Elevator " + i + " at floor: ", SwingConstants.CENTER);
            label.setFont(new Font("Arial", Font.BOLD, 20));
            elevatorLabels.add(label);
            elevatorPanel.add(label);
        }

        JPanel activePanel = new JPanel();
        activePanel.setLayout(new BorderLayout());
        activePanel.add(new JLabel("Active Requests:"), BorderLayout.NORTH);
        activeRequestsWindow.setEditable(false);
        activePanel.add(new JScrollPane(activeRequestsWindow), BorderLayout.CENTER);

        JPanel completedPanel = new JPanel();
        completedPanel.setLayout(new BorderLayout());
        completedPanel.add(new JLabel("Completed Requests:"), BorderLayout.NORTH);
        completedRequestsWindow.setEditable(false);
        completedPanel.add(new JScrollPane(completedRequestsWindow), BorderLayout.CENTER);

        JPanel centerPanel = new JPanel(new GridLayout(2, 1));
        centerPanel.add(activePanel);
        centerPanel.add(completedPanel);

        add(elevatorPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);

        system = new ElevatorManagement(numElevators, maxFloor, this);
        new Thread(() -> {
            while (true) {
                Request randomRequest = Request.generateRequest(maxFloor);
                system.handleRequest(randomRequest);
                try {
                    Thread.sleep(800);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }).start();

        setVisible(true);
    }

    public synchronized void elevatorInfo(int elevatorId, int currentFloor) {
        elevatorLabels.get(elevatorId).setText("Elevator " + (elevatorId + 1) + " at floor: " + currentFloor);
    }

    public synchronized void updateRequests(List<Request> requests) {
        StringBuilder builder = new StringBuilder();
        for (Request i : requests) {
            builder.append(i).append("\n");
        }
        activeRequestsWindow.setText(builder.toString());
    }

    public synchronized void addCompletedRequest(Request request, int elevatorId) {
        String completed = "Elevator " + (elevatorId + 1) + " completed " + request + "\n";
        completedRequestsWindow.append(completed);
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int elevators = 0;
        int maxFloor = 0;
        boolean validInput = false;

        while (!validInput) {
            try {
                System.out.println("Enter number of elevators: ");
                elevators = scanner.nextInt();
                System.out.println("Enter maximum floor: ");
                maxFloor = scanner.nextInt();
                validInput = true;
            } catch (InputMismatchException e) {
                System.out.println("Incorrect input. Only an integer can be entered.");
                scanner.nextLine();
            }
        }
        scanner.close();

        int finalElevators = elevators;
        int finalMaxFloor = maxFloor;
        SwingUtilities.invokeLater(() -> new ElevatorGUI(finalElevators, finalMaxFloor));
    }
}
