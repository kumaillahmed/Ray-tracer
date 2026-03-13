package raytracer.geom;

import raytracer.core.Hit;
import raytracer.core.Obj;
import raytracer.core.def.LazyHitTest;
import raytracer.math.Point;
import raytracer.math.Ray;
import raytracer.math.Vec2;
import raytracer.math.Vec3;

public class Sphere extends BBoxedPrimitive {

    private final Point center;
    private final float radius;

    public Sphere(Point center, float radius) {
        super(BBox.create(center.add(new Vec3(radius, radius, radius)), center.sub(new Vec3(radius, radius, radius))));
        this.center = center;
        this.radius = radius;
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
                if (point == null) {
                    point = ray.eval(t);
                }
                return point;
            }

            @Override
            protected boolean calculateHit() {
                Vec3 oc = ray.base().sub(center);
                float a = ray.dir().dot(ray.dir());
                float b = 2.0f * oc.dot(ray.dir());
                float c = oc.dot(oc) - radius * radius;
                float discriminant = b * b - 4 * a * c;

                if (discriminant < 0) {
                    return false;
                } else {
                    float t1 = (-b - (float) Math.sqrt(discriminant)) / (2.0f * a);
                    float t2 = (-b + (float) Math.sqrt(discriminant)) / (2.0f * a);

                    if (t1 > tmin && t1 < tmax) {
                        t = t1;
                        return true;
                    }

                    if (t2 > tmin && t2 < tmax) {
                        t = t2;
                        return true;
                    }

                    return false;
                }
            }

            @Override
            public Vec2 getUV() {
                Vec3 hitPoint = getPoint().sub(center).normalized();
                float u = 0.5f + (float) Math.atan2(hitPoint.z(), hitPoint.x()) / (2 * (float) Math.PI);
                float v = 0.5f - (float) Math.asin(hitPoint.y()) / (float) Math.PI;
                return new Vec2(u, v);
            }

            @Override
            public Vec3 getNormal() {
                return getPoint().sub(center).normalized();
            }
        };
    }

    @Override
    public int hashCode() {
        return center.hashCode() ^ Float.hashCode(radius);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Sphere) {
            Sphere sphere = (Sphere) other;
            return center.equals(sphere.center) && radius == sphere.radius;
        }
        return false;
    }
}
