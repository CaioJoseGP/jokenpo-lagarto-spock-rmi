import java.util.Scanner;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Cliente extends UnicastRemoteObject implements ICliente {
    private static volatile boolean liberado = false;
    private static volatile boolean resultado = false;

    public Cliente() throws RemoteException {
        super();
    }

    @Override 
    public boolean controlarJogada() throws RemoteException {
        if (liberado == true) {
            return liberado = false;
        }
        return liberado = true;
    }

    @Override
    public void enviarMensagem(String mensagem) throws RemoteException {
        System.out.println(mensagem);
    }

    @Override
    public void informarResultado(boolean ganhou) throws RemoteException {
        resultado = ganhou;
    }

    public static void main(String[] args) {
        String host = "localhost";
        int port = 12345;
        Scanner scan = new Scanner(System.in);

        try {
            Registry registry = LocateRegistry.getRegistry(host, port);
            IServidor server = (IServidor) registry.lookup("ServerJokenpo");
            
            Cliente meuCliente = new Cliente();
            int id = server.conectar(meuCliente);

            while (id != -1) {
                if (!liberado) {
                    Thread.sleep(200);
                } else {
                    System.out.println("========== ESCOLHA SUA JOGADA ==========");
                    System.out.println("1 - pedra"
                            + "\n2 - papel"
                            + "\n3 - tesoura"
                            + "\n4 - spock"
                            + "\n5 - lagarto"
                            + "\n6 - sair do jogo");
                    System.out.println("========================================");
                    
                    System.out.print("> ");
                    int option = scan.nextInt();
                    scan.nextLine();

                    if (liberado) {
                        switch (option) {
                            case 1:
                                server.enviarJogada(id, Jogada.PEDRA);
                                break;
                            case 2:
                                server.enviarJogada(id, Jogada.PAPEL);
                                break;
                            case 3:
                                server.enviarJogada(id, Jogada.TESOURA);
                                break;
                            case 4:
                                server.enviarJogada(id, Jogada.SPOCK);
                                break;
                            case 5:
                                server.enviarJogada(id, Jogada.LAGARTO);
                                break;
                            case 6:
                                server.desconectar(id);
                                break;
                            default:
                                System.out.println("Jogada inválida!");
                                break;
                        }

                        liberado = false; 
                    } else {
                        System.out.println("Tempo esgotado! Você demorou mais de 5 segundos.\n");
                    }

                    if (resultado) {
                        System.out.println("\n====== PROVOQUE SEUS INIMIGOS ======");
                        String provocacao = scan.nextLine();

                        if (resultado) {
                            server.provocar(id, provocacao);
                        } else {
                            System.out.println("Que pena, tempo esgotado.\n");
                        }
                    }

                    resultado = false;
                }
            }
            
            scan.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
