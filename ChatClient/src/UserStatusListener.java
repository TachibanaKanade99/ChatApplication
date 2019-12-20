public interface UserStatusListener {
    void Online(String client_name);
    void Offline(String client_name);
}
