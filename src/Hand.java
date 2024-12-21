import java.util.*;

public class Hand {
    private static final int HEARTS = 0;
    private static final int DIAMONDS = 1;
    private static final int SPADES = 2;
    private static final int CLUBS = 3;

    private static final int JACK = 11;
    private static final int QUEEN = 12;
    private static final int KING = 13;
    private static final int ACE = 14;

    private ArrayList<Card> cards;
    private boolean showAllCards;

    public Hand() {
        cards = new ArrayList<>();
        showAllCards = false;
    }

    public void addCard(Card c) {
        cards.add(c);
    }

    public int getValue() {
        int sum = 0;
        int aceCount = 0;

        for (Card c : cards) {
            sum += c.getValue();

            if (c.getRank() == ACE) {
                aceCount++;
            }
        }

        while (sum > 21 && aceCount > 0) {
            sum -= 10;
            aceCount--;
        }

        return sum;
    }

    public boolean hasBlackjack() {
        return getValue() == 21 && cards.size() == 2;
    }

    public boolean busted() {
        return getValue() > 21;
    }

    public boolean fiveCardCharlie() {
        return cards.size() == 5;
    }

    public void printDealerHand() {
        for (int i = 0; i < cards.size(); i++) {
            Card c = cards.get(i);

            if (i == 0 && !showAllCards) {
                System.out.print("X ");
            } else {
                System.out.print(c + " ");
            }
        }
        System.out.println();
    }

    public String toString() {
        StringBuilder result = new StringBuilder();

        for (Card c : cards) {
            result.append(c).append(" ");
        }

        result.append("(").append(getValue()).append(")");

        return result.toString();
    }

    // New method to get the cards
    public ArrayList<Card> getCards() {
        return cards;
    }


    public void revealCards() {
        showAllCards = true;
    }

    public Card removeFirstCard() {
        return cards.isEmpty() ? null : cards.remove(0);
    }

    public void addCardToFront(Card c) {
        cards.add(0, c);
    }
}
