/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.math.Vector3f;

/**
 *
 * @author Thad
 */
class BlockPosition {

    private int x,y,z;
    public BlockPosition(int x, int y, int z){
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public BlockPosition(float x, float y, float z){
        this((int)Math.round(x),(int)Math.round(y),(int)Math.round(z));
    }
public BlockPosition(Vector3f vector){
    this(vector.x, vector.y, vector.z);
}
    /**
     * @return the x
     */
    public int getX() {
        return x;
    }

    /**
     * @param x the x to set
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * @return the y
     */
    public int getY() {
        return y;
    }

    /**
     * @param y the y to set
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * @return the z
     */
    public int getZ() {
        return z;
    }

    /**
     * @param z the z to set
     */
    public void setZ(int z) {
        this.z = z;
    }
    
    @Override
    public boolean equals(Object o) {
            //   System.out.println("Comparing Blocks");
        //Should not be used to do < or >, only ==
        if (!(o instanceof BlockPosition)){
            System.out.println("Not an instance of blockpos");
            return false;
        }else{
            BlockPosition otherBlockPos = (BlockPosition)o;
                    if ((this.x == otherBlockPos.getX())&&(this.y == otherBlockPos.getY())&&(this.z == otherBlockPos.getZ())){
                       // System.out.println("EQUAL");
            return true;
        }else{
                       // System.out.println("Not EQUAL");
            return false;
        }
        }
    }
    
}
