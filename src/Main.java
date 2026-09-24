import javax.swing.*;
public class Main{
    public static void main(String[] args){
        DBHelper.setup();
        SwingUtilities.invokeLater(() ->{
            try{
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e){
            }
            MainWindow window = new MainWindow();
            window.setVisible(true);
        });
    }
}
