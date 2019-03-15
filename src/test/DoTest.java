import org.junit.Test;

public class DoTest {

    //简单阶乘的递归实现
    public long jiecheng(int num){
        if(num <=1){
            return 1;
        }
        else{
            return num*jiecheng(num-1);
        }
    }

    @Test
    public void test(){
        int res = 12;
        System.out.println(jiecheng(12));
    }

    @Test
    public void trimTest(){
        String str = "      xxxx   xxxx  xx    ";
        System.out.println("["+str.trim()+"]");
    }

//  res ：479001600
}
