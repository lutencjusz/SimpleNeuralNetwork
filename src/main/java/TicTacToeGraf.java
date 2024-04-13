import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TicTacToeGraf extends JFrame implements ActionListener {
    private JButton[][] buttons = new JButton[3][3];
    private boolean currentPlayer = true;  // true for X, false for O

    public TicTacToeGraf() {
        super("Kółko i Krzyżyk");
        setSize(300, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(3, 3));
        initializeButtons();
        resetButtons();
    }

    private void initializeButtons() {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                buttons[row][col] = new JButton();
                buttons[row][col].setFont(new Font("Courier New", Font.BOLD, 100));
                buttons[row][col].setFocusPainted(false);
                buttons[row][col].addActionListener(this);
                add(buttons[row][col]);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton buttonClicked = (JButton) e.getSource(); // gets the particular button that was clicked
        if (currentPlayer) {
            if (buttonClicked.getText().equals("")) {
                buttonClicked.setForeground(Color.RED);
                buttonClicked.setText("X");
                currentPlayer = false;
                checkForWin();
            }
        } else {
            if (buttonClicked.getText().equals("")) {
                buttonClicked.setForeground(Color.BLUE);
                buttonClicked.setText("O");
                currentPlayer = true;
                checkForWin();
            }
        }
    }

    private void checkForWin() {
        // Horizontal, vertical, and diagonal win checks
        for (int i = 0; i < 3; i++) {
            if (checkRowCol(buttons[i][0].getText(), buttons[i][1].getText(), buttons[i][2].getText()) ||
                    checkRowCol(buttons[0][i].getText(), buttons[1][i].getText(), buttons[2][i].getText()) ||
                    checkRowCol(buttons[0][0].getText(), buttons[1][1].getText(), buttons[2][2].getText()) ||
                    checkRowCol(buttons[0][2].getText(), buttons[1][1].getText(), buttons[2][0].getText())) {

                JOptionPane.showMessageDialog(null, "Gracz " + (currentPlayer ? "O" : "X") + " wygrał!");
                resetButtons();
                break;
            }
        }
    }

    private boolean checkRowCol(String c1, String c2, String c3) {
        return (!c1.equals("") && c1.equals(c2) && c2.equals(c3));
    }

    private void resetButtons() {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                buttons[row][col].setText("K");
            }
        }
        currentPlayer = true;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TicTacToeGraf().setVisible(true));
    }
}
