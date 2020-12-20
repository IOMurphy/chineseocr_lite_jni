package li.murphy.pojo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OcrResult{
    public double dbNetTime;
    public ArrayList<TextBlock> textBlocks;
    public double detectTime;
    public String strRes;
}

