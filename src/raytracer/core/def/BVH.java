package raytracer.core.def;

import java.util.ArrayList;
import java.util.List;

import raytracer.core.Hit;
import raytracer.core.Obj;
import raytracer.geom.BBox;
import raytracer.math.Point;
import raytracer.math.Ray;
import raytracer.math.Vec3;

/**
 * Represents a bounding volume hierarchy acceleration structure
 */
public class BVH extends BVHBase {
    private List<Obj> objects;
    private BBox bbox;

    public BVH() {
        this.objects = new ArrayList<>();
        this.bbox = null;
    }

    @Override
    public BBox bbox() {
        return bbox;
    }

    /**
     * Adds an object to the acceleration structure
     *
     * @param prim The object to add
     */
    @Override
    public void add(final Obj prim) {
        objects.add(prim);
        if (bbox == null) {
            bbox = prim.bbox();
        } else {
            bbox = BBox.surround(bbox, prim.bbox());
        }
    }

    /**
     * Builds the actual bounding volume hierarchy
     */
    @Override
    public void buildBVH() {
        if (objects.isEmpty()) {
            return;
        }

        // create a temporary list of objects for building the BVH
        List<Obj> tempObjects = new ArrayList<>(objects);

        // build the BVH recursively
        buildBVHRecursive(tempObjects, 0, tempObjects.size() - 1);
    }

    private void buildBVHRecursive(List<Obj> objects, int start, int end) {
        if (start >= end) {
            // base case: Only one object remaining
            add(objects.get(start));
        } else if (start + 1 == end) {
            // base case: Two objects remaining
            add(objects.get(start));
            add(objects.get(end));
        } else {
            // split the objects and build the BVH recursively

            // calculate the extent of the objects' bounding boxes
            BBox bBox = objects.get(start).bbox();
            for (int i = start + 1; i <= end; i++) {
                bBox = BBox.surround(bBox, objects.get(i).bbox());
            }

            // calculate the split dimension and position
            Vec3 extent = bBox.getMax().sub(bBox.getMin());
            int splitDim = calculateSplitDimension(extent);
            float splitPos = 0.5f * (bBox.getMin().get(splitDim) + bBox.getMax().get(splitDim));

            // partition the objects based on the split dimension and position
            int splitIndex = partitionObjects(objects, start, end, splitDim, splitPos);

            // recursively build the BVH for the two partitions
            BVHBase leftBVH = createBVH();
            BVHBase rightBVH = createBVH();
            buildBVHRecursive(objects, start, splitIndex);
            buildBVHRecursive(objects, splitIndex + 1, end);

            // distribute the objects to the left and right partitions
            distributeObjects(leftBVH, rightBVH, splitDim, splitPos);

            // add the left and right partitions as children of this BVH
            add(leftBVH);
            add(rightBVH);
        }
    }

    public int calculateSplitDimension(Vec3 extent) {
        if (extent.x() > extent.y() && extent.x() > extent.z()) {
            return 0; // split along the x-axis
        } else if (extent.y() > extent.z()) {
            return 1; // split along the y-axis
        } else {
            return 2; // split along the z-axis
        }
    }

    private int partitionObjects(List<Obj> objects, int start, int end, int splitDim, float splitPos) {
        int left = start;
        int right = end;
        while (left <= right) {
            while (left <= right && objects.get(left).bbox().getCentroid().get(splitDim) <= splitPos) {
                left++;
            }
            while (left <= right && objects.get(right).bbox().getCentroid().get(splitDim) > splitPos) {
                right--;
            }
            if (left < right) {
                swap(objects, left, right);
                left++;
                right--;
            }
        }
        return right;
    }

    private void swap(List<Obj> objects, int i, int j) {
        Obj temp = objects.get(i);
        objects.set(i, objects.get(j));
        objects.set(j, temp);
    }

    @Override
    public Hit hit(final Ray ray, final Obj obj, final float tMin, final float tMax) {
        Hit closestHit = Hit.MISS;

        if (bbox != null && bbox.hit(ray, tMin, tMax)) {
            for (Obj child : getChildren()) {
                Hit hit = child.hit(ray, obj, tMin, tMax);
                closestHit = Hit.min(closestHit, hit);
            }
        }

        return closestHit;
    }

    @Override
    public List<Obj> getObjects() {
        return objects;
    }
}
