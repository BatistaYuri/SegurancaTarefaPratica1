/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entidade;

import controller.ControllerUsuario;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.HashMap;

/**
 *
 * @author Yuri
 */
public class UsuarioDAO {
    private static UsuarioDAO instancia;
    private HashMap<String, Usuario> usuarios = new HashMap<>();
    private final String arquivoUsuarios = "Cadastros.txt";
   
    private UsuarioDAO() {
        load();
    }
    
    public Usuario get(String login){
        return this.usuarios.get(login);
    }
    
    public void put(Usuario usuario){
        this.usuarios.put(usuario.getLogin(), usuario);
        this.persist();
    }
    
    public void remove(String login){
	this.usuarios.remove(login);
	persist();
    }
    
    public void persist(){
        try{
           FileOutputStream fout = new FileOutputStream(arquivoUsuarios);
           ObjectOutputStream oo = new ObjectOutputStream(fout);
           oo.writeObject(this.usuarios);
           
           oo.flush();
           fout.flush();
            
           oo.close();
           fout.close();
           
           load();
            
        } catch (FileNotFoundException ex) {
            System.out.println(ex);
            persist();
        }catch (IOException ex) {
            System.out.println(ex);
        }
    }
    
    public void load () {
        try {
            FileInputStream fin = new FileInputStream(arquivoUsuarios);
            ObjectInputStream oi = new ObjectInputStream(fin);
        
            this.usuarios = (HashMap<String, Usuario>) oi.readObject();
            
            oi.close();
            fin.close();
        
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
            
        } catch (FileNotFoundException ex) {
            System.out.println(ex);
            persist();
        
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public static UsuarioDAO getInstancia(){
        if(instancia == null){
            instancia = new UsuarioDAO();
        }
        return instancia;
    }
    
    public Collection<Usuario> getList(){
        load();
	return usuarios.values();
    }
}
