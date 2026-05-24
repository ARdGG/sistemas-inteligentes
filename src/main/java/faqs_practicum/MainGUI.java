package faqs_practicum;

public class MainGUI extends Thread {

	private JFramePrincipal frame;
    private AgenteCliente agenteCliente;


    public MainGUI(AgenteCliente a){
        this.agenteCliente = a;
    }

	public void run(){

        frame = new JFramePrincipal(agenteCliente);
        frame.setTitle("Agente Cliente");
        frame.setVisible(true);
        frame.setResizable(true);

	}

    public JFramePrincipal getFrame() {
        return frame;
    }

}
