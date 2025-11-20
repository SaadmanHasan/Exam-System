import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;

public class TeacherTest {
    @Test
    void getStatistics1(){
        int [] expected = new int[]{52, 48, 100, 20, 10};
        int [] actual = Teacher.getQuizStatistics("COMP3111",1);
        assertArrayEquals(expected,actual);
    }

    @Test
    void getStatistics2(){
        int [] expected = new int[]{0,0,0,0,0};
        int [] actual = Teacher.getQuizStatistics("Rand",3);
        assertArrayEquals(expected,actual);
    }

}