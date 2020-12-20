package li.murphy.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class TextBlock {
    public ArrayList<Point> boxPoint;
    public float boxScore;
    public int angleIndex;
    public float angleScore;
    public double angleTime;
    public String text;
    public float [] charScores;
    public double crnnTime;
    public double blockTime;
}
