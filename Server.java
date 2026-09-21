import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.time.Duration;

public class Server extends UnicastRemoteObject implements IServidor {
    // private static List<ICliente> clientesConectados = new ArrayList<>();
    private static Map<Integer, ICliente> clientesConectados = new HashMap<>();
    private static Map<Integer, Jogada> rodada = new HashMap<>();
    private static Map<Integer, Integer> score = new HashMap<>();
    private static int empates = 0;

    public Server() throws RemoteException {
        super();
    }

    @Override
    public int conectar(ICliente interfaceDoCliente) throws RemoteException {
        try {
            if(clientesConectados.size() < 5) {
                int idCliente = clientesConectados.size();
                clientesConectados.put(idCliente, interfaceDoCliente);
                score.put(idCliente, 0);

                interfaceDoCliente.enviarMensagem("Conectado com sucesso!");

                return idCliente;
            } else {
                interfaceDoCliente.enviarMensagem("Número máximo de jogadores atingido.");
                return -1;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public void enviarJogada(int id, Jogada jogada) throws RemoteException {
        rodada.put(id, jogada);
    }

    @Override 
    public void provocar(int id, String mensagem) throws RemoteException {
        for (int i = 0; i < clientesConectados.size(); i++) {
            ICliente cliente = clientesConectados.get(i);

            if (cliente.equals(clientesConectados.get(id))) {
                continue;
            }
            cliente.enviarMensagem(mensagem);
        }
    }

    @Override
    public void desconectar(int id) throws RemoteException {
        ICliente cliente = clientesConectados.get(id);
        cliente.enviarMensagem("\nObrigado por jogar!");

        clientesConectados.remove(id);
        score.remove(id);
    }

    public static void main(String[] args) {
        int port = 12345;
        Duration tempoEntrePartidas = Duration.ofSeconds(10);
        Duration tempoEntreJogadas = Duration.ofSeconds(5);
        StringBuilder scoreTotal = new StringBuilder();

        try {
            Server server = new Server();

            Registry registry = LocateRegistry.createRegistry(port);
            registry.rebind("ServerJokenpo", server);

            while (true) {
                if (clientesConectados.size() < 2) {
                    Thread.sleep(1000);
                } else {
                    for (int i = 0; i < clientesConectados.size(); i++) {
                        clientesConectados.get(i).controlarJogada();
                        clientesConectados.get(i).enviarMensagem("Começando rodada. Você tem 5 segundos...");
                    }

                    rodada.clear();
                    scoreTotal.setLength(0);
                    Thread.sleep(tempoEntreJogadas);

                    for (int i = 0; i < clientesConectados.size(); i++) {
                        clientesConectados.get(i).controlarJogada();
                    }

                    List<Integer> idGanhadores = new ArrayList<>();
                    int jogadasValidas = 0;

                    for (int i = 0; i < rodada.size(); i++) {
                        Jogada jogada = rodada.get(i);
                        boolean foiDerrotado = false;

                        if (jogada == null) {
                            continue;
                        }

                        for (int x = 0; x < rodada.size(); x++) {
                            if (i == x) continue;

                            Jogada jogadaAdversaria = rodada.get(x);

                            if (jogadaAdversaria != null && jogadaAdversaria.ganhaDe(jogada)) {
                                foiDerrotado = true;
                                break;
                            }
                        } 

                        if (!foiDerrotado) {
                            idGanhadores.add(i);
                        }
                    }

                    if (idGanhadores.isEmpty() || idGanhadores.size() == jogadasValidas) {
                        empates++;
                        idGanhadores.clear();
                    } else {
                        for (int id : idGanhadores) {
                            score.put(id, score.get(id) + 1);
                        }

                        scoreTotal.append("\n");

                        for (int i = 0; i < score.size(); i++) {
                            scoreTotal.append("Jogador " + i + " --> Pontuação: " + score.get(i) + "\n");
                        }

                        scoreTotal.append("Empates: " + empates + "\n");
                    }

                    for (int i = 0; i < clientesConectados.size(); i++) {
                        ICliente cliente = clientesConectados.get(i);
            
                        if (idGanhadores.contains(i)) {
                            cliente.enviarMensagem(scoreTotal.toString());
                            cliente.informarResultado(true);
                        } else {
                            cliente.enviarMensagem(scoreTotal.toString());
                        }
                    }

                    Thread.sleep(tempoEntrePartidas);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
