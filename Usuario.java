//Universidad del valle Guatemala
// Gabriel Soto
//23900

public class Usuario {

        private String name;
        private String usuario;
        private String contraseña;
        private boolean esPremium;
    
        // Constructor
        public Usuario(String name, String usuario, String contraseña, boolean esPremium) {
            this.name = name;
            this.usuario = usuario;
            this.contraseña = contraseña;
            this.esPremium = esPremium;
        }
    
        // getters
        public String getUsuario() {
            return usuario;
        }
    
        public String getContraseña() {
            return contraseña;
        }
    
        public boolean esPremium() {
            return esPremium;
        }

        public String getname() {
            return name;
        }

        
    }
