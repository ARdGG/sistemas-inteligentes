package faqs_practicum;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class JFramePrincipal extends JFrame {
    private JTextField nombreAsignaturaTexto;
    private JTextField preguntaTexto;
    private JTextField direccionTexto;

    private JButton botonResumen;
    private JButton botonPracticum;
    private JButton botonLlegar;

    private JTextArea respuestaResumenTexto;

    private JTextArea respuestaPracticumTexto;

    private JTextArea respuestaLlegarTexto;

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    JFramePrincipal window = new JFramePrincipal();
                    window.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    /**
     * Create the application.
     */
    public JFramePrincipal() {}

    public JFramePrincipal(AgenteCliente agenteCliente) {
        initialize(agenteCliente);
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize(AgenteCliente a) {
        setBounds(100, 100, 434, 435);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        getContentPane().add(tabbedPane, BorderLayout.CENTER);

        JPanel asignaturas = new JPanel();
        tabbedPane.addTab("Resumen Asignaturas", null, asignaturas, null);

        JPanel datosAsignatura = new JPanel();
        datosAsignatura.setToolTipText("Prueba");
        Border padding = BorderFactory.createEmptyBorder(5, 10, 5, 10);
        Border titledBorderDatosResumen = new TitledBorder(null, "Datos", TitledBorder.LEADING, TitledBorder.TOP, null, null);
        Border finalBorderDatosResumen = BorderFactory.createCompoundBorder(titledBorderDatosResumen, padding);
        datosAsignatura.setBorder(finalBorderDatosResumen);
        asignaturas.setLayout(new BorderLayout(0, 0));
        datosAsignatura.setLayout(new BorderLayout(5, 5));

        JLabel lblNewLabel = new JLabel("Nombre Asignatura:");
        lblNewLabel.setToolTipText("Introduce el nombre de la asignatura");
        datosAsignatura.add(lblNewLabel, BorderLayout.WEST);

        nombreAsignaturaTexto = new JTextField();
        nombreAsignaturaTexto.setToolTipText("Introduce el nombre de la asignatura");
        datosAsignatura.add(nombreAsignaturaTexto, BorderLayout.CENTER);
        nombreAsignaturaTexto.setColumns(20);
        asignaturas.add(datosAsignatura, BorderLayout.NORTH);

        JPanel panel_2 = new JPanel();
        FlowLayout flowLayout = (FlowLayout) panel_2.getLayout();
        flowLayout.setVgap(0);
        flowLayout.setHgap(0);
        flowLayout.setAlignment(FlowLayout.RIGHT);
        panel_2.setBorder(null);
        datosAsignatura.add(panel_2, BorderLayout.SOUTH);

        botonResumen = new JButton("Buscar");
        botonResumen.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cambiarEstadoBotones(false);
                String nombreAsignatura = nombreAsignaturaTexto.getText();
                a.texto = nombreAsignatura;
                a.accion = 0;
                a.doWake();
            }
        });
        panel_2.add(botonResumen);



        JPanel respuestaResumen = new JPanel();
        respuestaResumen.setBorder(new TitledBorder(null, "Resumen", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        asignaturas.add(respuestaResumen, BorderLayout.CENTER);
        respuestaResumen.setLayout(new BorderLayout(0, 0));

        respuestaResumenTexto = new JTextArea();
        respuestaResumenTexto.setRows(10);
        respuestaResumenTexto.setLineWrap(true);
        respuestaResumenTexto.setEditable(false);
        respuestaResumenTexto.setColumns(53);

        JScrollPane scrollPane = new JScrollPane(respuestaResumenTexto);
        respuestaResumen.add(scrollPane);

        JPanel preguntas = new JPanel();
        tabbedPane.addTab("Practicum", null, preguntas, null);
        preguntas.setLayout(new BorderLayout(0, 0));

        JPanel pregunta = new JPanel();
        pregunta.setToolTipText("Prueba");
        preguntas.add(pregunta, BorderLayout.NORTH);
        Border titledBorderPreguntaPracticum = new TitledBorder(null, "Pregunta", TitledBorder.LEADING, TitledBorder.TOP, null, null);
        Border finalBorderPreguntaPracticum = BorderFactory.createCompoundBorder(titledBorderPreguntaPracticum, padding);
        pregunta.setLayout(new BorderLayout(5, 5));
        pregunta.setBorder(finalBorderPreguntaPracticum);

        JLabel lblNewLabel_1 = new JLabel("Nombre Asignatura:");
        lblNewLabel_1.setToolTipText("Introduce el nombre de la asignatura");
        pregunta.add(lblNewLabel_1, BorderLayout.NORTH);

        preguntaTexto = new JTextField();
        preguntaTexto.setToolTipText("Introduce el nombre de la asignatura");
        preguntaTexto.setColumns(20);
        pregunta.add(preguntaTexto, BorderLayout.CENTER);

        JPanel panel_2_1 = new JPanel();
        FlowLayout flowLayout_1 = (FlowLayout) panel_2_1.getLayout();
        flowLayout_1.setVgap(0);
        flowLayout_1.setHgap(0);
        flowLayout_1.setAlignment(FlowLayout.RIGHT);
        panel_2_1.setBorder(null);
        pregunta.add(panel_2_1, BorderLayout.SOUTH);

        botonPracticum = new JButton("Buscar");
        botonPracticum.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cambiarEstadoBotones(false);
                String pregunta = preguntaTexto.getText();
                a.texto = pregunta;
                a.accion = 1;
                a.doWake();
            }
        });
        panel_2_1.add(botonPracticum);

        JPanel respuestaPracticum = new JPanel();
        respuestaPracticum.setBorder(new TitledBorder(null, "Respuesta", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        preguntas.add(respuestaPracticum, BorderLayout.CENTER);
        respuestaPracticum.setLayout(new BorderLayout(0, 0));

        respuestaPracticumTexto = new JTextArea();
        respuestaPracticumTexto.setRows(10);
        respuestaPracticumTexto.setLineWrap(true);
        respuestaPracticumTexto.setEditable(false);
        respuestaPracticumTexto.setColumns(53);
        respuestaPracticum.add(respuestaPracticumTexto, BorderLayout.SOUTH);

        JScrollPane scrollPane_1 = new JScrollPane(respuestaPracticumTexto);
        respuestaPracticum.add(scrollPane_1, BorderLayout.CENTER);

        JPanel llegar = new JPanel();
        tabbedPane.addTab("Como Llegar", null, llegar, null);
        llegar.setLayout(new BorderLayout(0, 0));

        JPanel datosDireccion = new JPanel();
        datosDireccion.setBorder(new CompoundBorder(new TitledBorder(null, "Direccion", TitledBorder.LEADING, TitledBorder.TOP, null, null), padding));
        datosDireccion.setToolTipText("Prueba");
        llegar.add(datosDireccion, BorderLayout.NORTH);
        datosDireccion.setLayout(new BorderLayout(5, 5));

        JLabel lblNewLabel_1_1 = new JLabel("Dirección de inicio:");
        lblNewLabel_1_1.setToolTipText("Introduce el nombre de la asignatura");
        datosDireccion.add(lblNewLabel_1_1, BorderLayout.NORTH);

        direccionTexto = new JTextField();
        direccionTexto.setToolTipText("Introduce el nombre de la asignatura");
        direccionTexto.setColumns(20);
        datosDireccion.add(direccionTexto, BorderLayout.CENTER);

        JPanel panel_2_1_1 = new JPanel();
        FlowLayout flowLayout_2 = (FlowLayout) panel_2_1_1.getLayout();
        flowLayout_2.setVgap(0);
        flowLayout_2.setHgap(0);
        flowLayout_2.setAlignment(FlowLayout.RIGHT);
        panel_2_1_1.setBorder(null);
        datosDireccion.add(panel_2_1_1, BorderLayout.SOUTH);

        botonLlegar = new JButton("Buscar Ruta");
        botonLlegar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cambiarEstadoBotones(false);
                String direccion = direccionTexto.getText();
                a.texto = direccion;
                a.accion = 2;
                a.doWake();
            }
        });
        panel_2_1_1.add(botonLlegar);

        JPanel respuestaLlegar = new JPanel();
        respuestaLlegar.setBorder(new TitledBorder(null, "Ruta Explicada", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        llegar.add(respuestaLlegar, BorderLayout.CENTER);
        respuestaLlegar.setLayout(new BorderLayout(0, 0));


        respuestaLlegarTexto = new JTextArea();
        respuestaLlegarTexto.setRows(10);
        respuestaLlegarTexto.setLineWrap(true);
        respuestaLlegarTexto.setEditable(false);
        respuestaLlegarTexto.setColumns(53);

        JScrollPane scrollPane_1_1 = new JScrollPane(respuestaLlegarTexto);
        respuestaLlegar.add(scrollPane_1_1, BorderLayout.CENTER);

    }

    public JTextArea getRespuestaPracticumTexto() {
        return respuestaPracticumTexto;
    }

    public JTextArea getRespuestaLlegarTexto() {
        return respuestaLlegarTexto;
    }

    public JTextArea getRespuestaResumenTexto() {
        return respuestaResumenTexto;
    }

    public void cambiarEstadoBotones(boolean estado){
        botonLlegar.setEnabled(estado);
        botonResumen.setEnabled(estado);
        botonPracticum.setEnabled(estado);
    }
}
