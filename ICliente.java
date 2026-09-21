import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ICliente extends Remote {
    boolean controlarJogada() throws RemoteException;
    void enviarMensagem(String mensagem) throws RemoteException;
    void informarResultado(boolean ganhou) throws RemoteException;
}
