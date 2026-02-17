package server;

public class ErrorResponse {
    int status;
    String error;
    public ErrorResponse(String error, int status){
        this.error = error;
        this.status = status;
    }
}
