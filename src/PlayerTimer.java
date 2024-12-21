import java.util.Timer;
import java.util.TimerTask;
import java.util.Scanner;

public class PlayerTimer {
    private boolean inputReceived;
    private String playerMove;

    public PlayerTimer() {
        inputReceived = false;
        playerMove = null;
    }

    public String getPlayerMove() {
        return getPlayerMoveHelper("Choose your move: (HIT/STAND)");
    }

    public String getPlayerMoveWithAbility() {
        return getPlayerMoveHelper("Choose your move: (HIT/STAND/USE)");
    }

    private String getPlayerMoveHelper(String prompt) {
        inputReceived = false;
        playerMove = null;

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!inputReceived) {
                    System.out.println("Time's up! No move received.");
                }
            }
        }, Blackjack.TIMEOUT_SECONDS * 1000);

        System.out.println(prompt);
        Scanner scanner = new Scanner(System.in);

        Thread inputThread = new Thread(() -> {
            if (scanner.hasNextLine()) {
                playerMove = scanner.nextLine();
                inputReceived = true;
                timer.cancel();
            }
        });

        inputThread.start();

        try {
            inputThread.join(Blackjack.TIMEOUT_SECONDS * 1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return inputReceived ? playerMove.toLowerCase() : null;
    }
}
