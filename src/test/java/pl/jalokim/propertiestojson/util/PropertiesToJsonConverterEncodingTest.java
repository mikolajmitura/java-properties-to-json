package pl.jalokim.propertiestojson.util;

import java.nio.charset.StandardCharsets;
import org.assertj.core.api.Assertions;
import org.junit.Test;

public class PropertiesToJsonConverterEncodingTest {

    @Test
    public void shouldManageChineseCharacters() {
        String json = PropertiesToJsonConverterBuilder.builder().charset(StandardCharsets.UTF_8).build()
            .convertPropertiesFromFileToJson("src/test/resources/encoding/messages_zh_TW.properties");
        Assertions.assertThat(json).contains("我接受使用條款");
    }

    @Test
    public void shouldNotManageChineseCharactersDueToDifferentEncoding() {
        String json = PropertiesToJsonConverterBuilder.builder().charset(StandardCharsets.ISO_8859_1).build()
            .convertPropertiesFromFileToJson("src/test/resources/encoding/messages_zh_TW.properties");
        Assertions.assertThat(json).doesNotContain("我接受使用條款");
    }
}
