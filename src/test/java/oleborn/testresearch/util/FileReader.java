package oleborn.testresearch.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;

@Component
@RequiredArgsConstructor
public class FileReader {

    private final ObjectMapper objectMapper;

    @SneakyThrows
    public <T> T readFile(String fileName, Class<T> clazz) {
        Path path = new ClassPathResource(fileName).getFile().toPath();
        String content = Files.readString(path);
        return objectMapper.readValue(content, clazz);
    }

}
