    import java.util.Scanner;

    public class Blackjack {
        private static final int INITIAL_WALLET = 100;
        public static final int TIMEOUT_SECONDS = 10;
        private static final int ABILITY_COST = 50;

        private Scanner scanner;
        private boolean abilityPurchased;
        private boolean abilityUsed;

        public Blackjack() {
            scanner = new Scanner(System.in);
            abilityPurchased = false;
            abilityUsed = false;
        }

        private int getPlayerWager(double wallet) {
            while (true) {
                System.out.println("Your wallet: $" + wallet);
                System.out.print("Enter your bet: ");
                int bet;
                try {
                    bet = Integer.parseInt(scanner.nextLine());
                    if (bet > 0 && bet <= wallet) return bet;
                    System.out.println("Invalid bet. Bet must be between $1 and $" + wallet);
                } catch (NumberFormatException e) {
                    System.out.println("Please enter a valid number.");
                }
            }
        }

        private boolean askToBuyAbility(double wallet) {
            if (wallet >= ABILITY_COST) {
                System.out.print("Do you want to buy the 'Peek-and-Swap' ability for $50? (Y/N): ");
                String response = scanner.nextLine();
                if (response.equalsIgnoreCase("Y")) {
                    System.out.println("Ability purchased!");
                    return true;
                }
            }
            return false;
        }

        private double playRound(double wallet) {
            int bet = getPlayerWager(wallet);

            abilityPurchased = askToBuyAbility(wallet);
            if (abilityPurchased) wallet -= ABILITY_COST;

            Deck deck = new Deck();
            deck.shuffle();

            Hand player = new Hand();
            Hand dealer = new Hand();

            player.addCard(deck.deal());
            dealer.addCard(deck.deal());
            player.addCard(deck.deal());
            dealer.addCard(deck.deal());

            System.out.println("Player's Hand: " + player);
            dealer.printDealerHand();

            boolean busted = playerTurn(player, deck, dealer);

            if (busted) {
                System.out.println("You busted :(");
            } else {
                System.out.println("Dealer's Turn...");
                dealer.revealCards();
                dealerTurn(dealer, deck);
            }

            double result = findWinner(dealer, player, bet);
            wallet += result;
            System.out.println("New wallet: $" + wallet);

            return wallet;
        }

        private boolean playerTurn(Hand player, Deck deck, Hand dealer) {
            PlayerTimer timer = new PlayerTimer();
            int turnCount = 0;

            while (true) {
                turnCount++;
                System.out.println("You have 10 seconds to make your move.");

                String move = (turnCount == 2 && abilityPurchased && !abilityUsed)
                        ? timer.getPlayerMoveWithAbility()
                        : timer.getPlayerMove();

                if (move == null) { // Timeout occurred
                    System.out.println("Too slow! Your turn is over.");
                    return false; // Pass the turn to the dealer
                }

                if (move.equals("hit")) {
                    Card c = deck.deal();
                    player.addCard(c);
                    System.out.println("Your card: " + c);
                    System.out.println("Player's Hand: " + player);
                    if (player.busted()) return true;
                } else if (move.equals("stand")) {
                    return false;
                } else if (move.equals("use") && turnCount == 2 && abilityPurchased && !abilityUsed) {
                    abilityUsed = true;
                    usePeekAndSwapAbility(player, dealer);
                } else {
                    System.out.println("Invalid move. Try again.");
                }
            }
        }

        private void usePeekAndSwapAbility(Hand player, Hand dealer) {
            System.out.println("Dealer's Cards: " + dealer);

            System.out.print("Do you want to swap cards with the dealer? (Y/N): ");
            String swapResponse = scanner.nextLine();
            if (swapResponse.equalsIgnoreCase("N")) {
                System.out.println("You chose not to swap cards.");
                return;
            }

            System.out.println("Dealer's Cards: " + dealer);
            System.out.println("Your Cards: " + player);

            System.out.print("Enter the index of the dealer's card to take (1-based): ");
            int dealerCardIndex = Integer.parseInt(scanner.nextLine()) - 1;

            System.out.print("Enter the index of your card to give (1-based): ");
            int playerCardIndex = Integer.parseInt(scanner.nextLine()) - 1;

            if (dealerCardIndex >= 0 && dealerCardIndex < dealer.getCards().size() &&
                    playerCardIndex >= 0 && playerCardIndex < player.getCards().size()) {

                Card dealerCard = dealer.getCards().get(dealerCardIndex);
                Card playerCard = player.getCards().get(playerCardIndex);

                dealer.getCards().set(dealerCardIndex, playerCard);
                player.getCards().set(playerCardIndex, dealerCard);

                System.out.println("Cards swapped successfully!");
                System.out.println("New Dealer's Hand: " + dealer);
                System.out.println("New Player's Hand: " + player);
            } else {
                System.out.println("Invalid card indices. No cards were swapped.");
            }

            System.out.print("Press ENTER to proceed.");
            scanner.nextLine();
        }

        private void dealerTurn(Hand dealer, Deck deck) {
            while (dealer.getValue() < 17) {
                System.out.println("Dealer hits.");
                dealer.addCard(deck.deal());
                System.out.println("Dealer's Hand: " + dealer);
                if (dealer.busted()) {
                    System.out.println("Dealer busted!");
                    return;
                }
            }
            System.out.println("Dealer stands.");
        }

        private double findWinner(Hand dealer, Hand player, int bet) {
            System.out.println("Dealer's Final Hand: " + dealer);
            if (player.busted()) {
                System.out.println("Dealer wins!");
                return -bet;
            } else if (dealer.busted() || player.getValue() > dealer.getValue()) {
                System.out.println("Player wins!");
                return player.hasBlackjack() ? 1.5 * bet : bet;
            } else if (player.getValue() == dealer.getValue()) {
                System.out.println("It's a push!");
                return 0;
            } else {
                System.out.println("Dealer wins!");
                return -bet;
            }
        }

        public void run() {
            System.out.println("Welcome to Blackjack!");
            System.out.println("RULE: You have " + TIMEOUT_SECONDS + " seconds to make your move. If you fail, your turn will be skipped.");
            double wallet = INITIAL_WALLET;
            while (wallet > 0) {
                wallet = playRound(wallet);
                if (wallet <= 0) {
                    System.out.println("You are out of money. Game over!");
                    break;
                }
                System.out.println("Play again?");
                System.out.print("ENTER for YES / N for NO:");
                if (scanner.nextLine().equalsIgnoreCase("N")) break;
            }
            System.out.println("Thanks for playing! Final wallet: $" + wallet);
        }

        public static void main(String[] args) {
            new Blackjack().run();
        }
    }
