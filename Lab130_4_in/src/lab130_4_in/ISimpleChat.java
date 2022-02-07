/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lab130_4_in;

/**
 *
 * @author Shwartskopff
 */
public interface ISimpleChat extends AutoCloseable{
    public static final int SERVER_PORT = 45678;
    public static final int BUFFER_SIZE = 4096;

    void client() throws ChatException;
    void server() throws ChatException;
    void getMessage() throws ChatException;
    void sendMessage(String message) throws ChatException;

    @Override
    public void close() throws ChatException;
    
}
