package Model;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * RSA
 */
public class RSA {
    int p,q,n,z,d = 0,e,i;
    public RSA(){
        p = 3;
        q = 11;
        n = p * q;
        z = (p-1) * (q-1);
        for (e = 2; e < z; e++) {
 
            // e is for public key exponent
            if (gcd(e, z) == 1) {
                break;
            }
        }
        for (i = 0; i <= 9; i++) {
            int x = 1 + (i * z);
 
            // d is for private key exponent
            if (x % e == 0) {
                d = x / e;
                break;
            }
        }
    }

    static int gcd(int e, int z)
    {
        if (e == 0)
            return z;
        else
            return gcd(z % e, e);
    }

    public double [] encode (String text){
        char [] symbols = text.toCharArray();
        int [] symbolNumbers = new int[symbols.length];
        for (int i = 0; i < symbolNumbers.length; i++) {
            symbolNumbers[i] = (int) symbols[i];
        }
        fromASCII(symbols,symbolNumbers);
        double [] encrypted = new double[symbols.length];
        for (int i = 0; i < symbols.length; i++) {
            encrypted[i] = Math.pow(symbolNumbers[i], this.e) % this.n;
        }
        return encrypted;
    }

    public void decode (double [] encrypted){
        int [] decrypted = new int[encrypted.length];
        for (int i = 0; i < encrypted.length; i++) {
            decrypted[i] = (int) (Math.pow(encrypted[i], this.d) % this.n);
        }
        char [] symbols = new char[decrypted.length];
        toASCII(symbols,decrypted);
        for (int i = 0; i < symbols.length; i++) {
            System.out.print(symbols[i]);
        }
    }

    private void fromASCII (char [] arr, int [] nArr){
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] >= 65 && arr[i] <= 90) {
                nArr[i] = arr[i] - 64;
            }
            else if (arr[i] >= 97 && arr[i] <= 122) {
                nArr[i] = arr[i] - 96;
            }
            else if (arr[i]>= 48 && arr[i] <= 57){
                nArr[i] = arr[i] - 48 + 31;
            }
            else if (arr[i] == 32) {
                nArr[i] = 0;
            }
            else if (arr[i] == '='){
                nArr[i] = 27;
            }
            else if(arr[i] == '-'){
                nArr[i] = 28;
            }
            else if (arr[i] == '"'){
                nArr[i] = 29;
            }
            else if (arr[i] == '\n'){
                nArr[i] = 30;
            }
        }
    }

    private void toASCII (char [] arr, int [] nArr){
        for (int i = 0; i < arr.length; i++) {
            if (nArr[i] >= 1 && nArr[i] <= 26) {
                arr[i] = (char) (nArr[i] + 64);
            }
            else if (nArr[i] >= 31 && nArr[i] <= 40) {
                
                arr[i] = (char) (nArr[i] - 31 + 48);
            }
            else if (nArr[i] == 0) {
                arr[i] = ' ';
            }
            else if (nArr[i] == 27){
                arr[i] = '=';
            }
            else if(nArr[i] == 28){
                arr[i] = '-';
            }
            else if (nArr[i] == 29){
                arr[i] = '"';
            }
            else if (nArr[i] == 30){
                arr[i] = '\n';
            }
        }
    }

    public static void main(String[] args) {
        RSA rsa = new RSA();
        double [] enc = rsa.encode("01234");
        rsa.decode(enc);

    }
}