/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Logic;


public class LoginState {
    
    private int state;
    private String stamp;

    public LoginState(int state, String stamp) {
        this.state = state;
        this.stamp = stamp;
    }

    public int getState() {
        return state;
    }

    public String getStamp() {
        return stamp;
    }
    
}
