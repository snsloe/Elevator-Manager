import java.util.Random;

public class Request {
    private static int counter = 0;
    private int id;
    private int startFloor;
    private int finishFloor;

    public Request(int startFloor, int finishFloor) {
        this.id = ++counter;
        this.startFloor = startFloor;
        this.finishFloor = finishFloor;
    }

    public int getId() {
        return id;
    }

    public int getStartFloor() {
        return startFloor;
    }

    public int getFinishFloor() {
        return finishFloor;
    }

    public static Request generateRequest(int maxFloor) {
        maxFloor = maxFloor - 1;
        Random random = new Random();
        int startFloor = random.nextInt(maxFloor) + 1;
        int finishFloor;


        while (true) {
            finishFloor = random.nextInt(maxFloor) + 1;
            if (finishFloor != startFloor) {
                break;
            }
        }
        return new Request(startFloor, finishFloor);
    }



    public String toString() {
        return "Request " + id + ": From " + startFloor + " to " + finishFloor + " floor.";
    }
}
