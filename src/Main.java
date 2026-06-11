import com.redes.ui.PantallaPrincipal;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // SwingUtilities.invokeLater asegura que la interfaz gráfica
        // se ejecute en el hilo correcto (Event Dispatch Thread)
        SwingUtilities.invokeLater(() -> {
            PantallaPrincipal ventana = new PantallaPrincipal();
            ventana.setVisible(true);
        });
    }
}