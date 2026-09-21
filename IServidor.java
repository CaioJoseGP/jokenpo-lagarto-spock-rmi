import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IServidor extends Remote {
    int conectar(ICliente interfaceCliente) throws RemoteException;
    void enviarJogada(int id, Jogada jogada) throws RemoteException;
    void provocar(int id, String mensagem) throws RemoteException;
    void desconectar(int id) throws RemoteException;
}
