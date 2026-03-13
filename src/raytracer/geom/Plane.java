package raytracer.geom;

import raytracer.core.Hit;
import raytracer.core.Obj;
import raytracer.core.def.LazyHitTest;
import raytracer.math.Constants;
import raytracer.math.Point;
import raytracer.math.Ray;
import raytracer.math.Vec2;
import raytracer.math.Vec3;

public class Plane extends BBoxedPrimitive {
    private final Point m;
    private final Vec3 n;

    public Plane(Point point, Vec3 normal) {
        super(BBox.INF); // Plane does not have a finite bounding box
        this.m = point;
        this.n = normal.normalized();
    }

    @Override
    public Hit hitTest(Ray ray, Obj obj, float tmin, float tmax) {
        return new LazyHitTest(obj) {
            private Point point = null;
            private float t;

            @Override
            public float getParameter() {
                return t;
            }

            @Override
            public Point getPoint() {
                if (point == null)
                    point = ray.eval(t).add(n.scale(Constants.EPS));
                return point;
            }

            @Override
            protected boolean calculateHit() {
                float denom = n.dot(ray.dir());
                if (Constants.isZero(denom))
                    return false;

                t = n.dot(m.sub(ray.base())) / denom;
                return t >= tmin && t <= tmax;
            }

            @Override
            public Vec2 getUV() {
                return Util.computePlaneUV(n, m, getPoint());
            }

            @Override
            public Vec3 getNormal() {
                return n;
            }
        };
    }

    @Override
    public int hashCode() {
        return m.hashCode() ^ n.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Plane) {
            Plane other = (Plane) obj;
            return other.m.equals(m) && other.n.equals(n);
        }
        return false;
    }
}

