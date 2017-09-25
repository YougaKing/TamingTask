package youga.tamingtask;

import java.util.ArrayList;
import java.util.List;

/**
 * @author YougaKingWu
 * @descibe ...
 * @date 2017/9/25 0025-9:58
 */

public class Root {


    public List<XXOO> list;
    public String name;
    public XXOO _$300414;
    public XXOO _$002182;
    public XXOO LMNR;
    public XXOO 我乐家居;

    public static class XXOO {
        public String n;
        public String i;
    }



    public String getName() {
        return name == null ? "" : name;
    }

    public List<XXOO> getList() {
        return list == null ? new ArrayList<XXOO>() : list;
    }
}
