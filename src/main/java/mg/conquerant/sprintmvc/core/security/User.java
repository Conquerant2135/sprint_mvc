package mg.conquerant.sprintmvc.core.security;

public class User {
    private String role;
    
    public final void setRole(String role){
        this.role = role;
    }

    public final String getRole(){
        return this.role;
    }
}