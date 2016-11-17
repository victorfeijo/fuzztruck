package remoteDriver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.StringTokenizer;

import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.plot.JFuzzyChart;
import net.sourceforge.jFuzzyLogic.rule.Variable;
import net.sourceforge.jFuzzyLogic.FunctionBlock;

public class RemoteDriver {

  static int port = 4321;
  static String host = "localhost";

    public static void main(String[] args) throws IOException {

        Socket kkSocket = null;
        PrintWriter out = null;
        BufferedReader in = null;

        try {
            kkSocket = new Socket(host, port);
            out = new PrintWriter(kkSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(kkSocket.getInputStream()));
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host:"  + host);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to: " + host);
            e.printStackTrace();
            System.exit(1);
        }

        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        String fromServer;

        double x, y;
        double angle;

        String filename = "remoteDriverManual/src/remoteDriver/driver.fcl";
        FIS fis = FIS.load(filename, true);

        JFuzzyChart.get().chart(fis);

        // Error while loading?
        if( fis == null ) {
            System.err.println("Can't load file: '" + filename + "'");
            return;
        }

        // requisicao da posicao do caminhao
        out.println("r");
        while ((fromServer = in.readLine()) != null) {
          StringTokenizer st = new StringTokenizer(fromServer);
          x = Double.valueOf(st.nextToken()).doubleValue();
          y = Double.valueOf(st.nextToken()).doubleValue();
          angle = Double.valueOf(st.nextToken()).doubleValue();

          System.out.println("x: " + x + " y: " + y + " angle: " + angle);


          // TODO sua lógica fuzzy vai aqui use os valores de x,y e angle obtidos. x e y estao em [0,1] e angulo [0,360)


          fis.setVariable("x", x);
          fis.setVariable("y", y);
          fis.setVariable("angle", angle);

          fis.evaluate();

          Variable turn = fis.getVariable("turn");

          double teste = turn.getLatestDefuzzifiedValue();

          double respostaDaSuaLogica = teste; // atribuir um valor entre -1 e 1 para virar o volante pra esquerda ou direita.

          ///////////////////////////////////////////////////////////////////////////////// Acaba sua modificacao aqui
          // envio da acao do volante
          out.println(respostaDaSuaLogica);

          // requisicao da posicao do caminhao
          out.println("r");
        }

        System.out.println("Close connection");
        out.close();
        in.close();
        stdIn.close();
        kkSocket.close();
    }
}
