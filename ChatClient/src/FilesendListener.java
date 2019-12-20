import java.io.IOException;

public interface FilesendListener {
    void onFile(String client_name, String file_name, String file_content) throws IOException;
}
