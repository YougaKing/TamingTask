package youga.tamingtask;

import org.junit.Test;

import java.util.Arrays;
import java.util.StringTokenizer;

/**
 * @author YougaKingWu
 * @descibe ...
 * @date 2017/9/25 0025-18:09
 */

public class TokenizerTest {


    @Test
    public void Tokenizer() {
        StringBuilder builder = new StringBuilder();
        builder.append("A");
        builder.append("M").append("HelloWorld")
                .append("Append").append("END");


        StringTokenizer st = new StringTokenizer(builder.toString());
        String[] cmdarray = new String[st.countTokens()];
        for (int i = 0; st.hasMoreTokens(); i++) {
            cmdarray[i] = st.nextToken();
        }

        System.out.println(Arrays.toString(cmdarray));

    }
}
