# Ray-tracer

A basic ray tracer implemented in Java.

This project renders 3D scenes by casting rays from a virtual camera into the scene and computing intersections with objects. It focuses on clean object-oriented design, geometric reasoning, and performance-aware rendering using acceleration structures. The implementation follows a standard ray tracing pipeline with primitives, shading, and a bounding volume hierarchy (BVH) for efficient rendering.

## Overview

Ray tracing simulates how light interacts with objects. Instead of simulating all light paths, this implementation casts rays from the camera into the scene and determines the color of each pixel based on intersections and lighting.

The pipeline is straightforward:

1. cast a ray per pixel
2. find the closest object intersection
3. compute color using a shader
4. repeat for all pixels

## Features

* Ray-object intersection (sphere, plane, triangle)
* Phong shading (ambient, diffuse, specular)
* Checkerboard and single-color shaders
* Shadow rays for basic illumination
* OBJ file loading (triangle meshes)
* Bounding Volume Hierarchy (BVH) for acceleration
* Modular design for geometry, shading, and core rendering

## Project Structure

```text
.
├── raytracer.core/        # Rendering pipeline
├── raytracer.geom/        # Geometric objects (sphere, plane, triangle)
├── raytracer.shade/       # Shaders (Phong, checkerboard, etc.)
├── raytracer.math/        # Vector and ray utilities
├── tests/                 # JUnit tests
└── Main                   # Entry point
```

The code is split so that geometry, shading, and rendering remain independent.

## Design

### Ray Tracing Model

Each pixel corresponds to a ray cast from the camera. The closest intersection determines the visible surface, and shading computes the final color.

### Geometry

* **Sphere**: solved using quadratic equation
* **Plane**: solved using ray-plane intersection
* **Triangle**: used for mesh-based models

Each object implements a `hit` method to determine intersections.

### Shading

The Phong model combines:

* ambient light (constant base)
* diffuse reflection (angle-dependent)
* specular reflection (highlights)

Shadow rays determine whether light reaches a surface point.

### Acceleration (BVH)

A bounding volume hierarchy reduces the number of intersection tests.

* objects are grouped into bounding boxes
* boxes are split recursively
* rays skip entire regions when no intersection occurs

This reduces complexity from linear to near-logarithmic in practice.

### OBJ Loading

Triangle meshes are loaded from OBJ files. Only vertex (`v`) and face (`f`) definitions are required, keeping parsing simple and predictable.

## Complexity

| Operation               |              Complexity |
| ----------------------- | ----------------------: |
| Ray-object intersection |                    O(n) |
| BVH traversal           |               ~O(log n) |
| Shading                 | O(l) (number of lights) |

BVH significantly improves performance for larger scenes.

## Edge Cases

* rays parallel to planes
* no intersection (background color)
* objects behind the camera
* shadow occlusion
* floating-point precision issues (handled via EPS)

## Running the Project

Open the project in VSCode and run the main class.

You can enable or disable scene components (e.g. spheres, shaders) via flags in the main file.

The output is a rendered image of the scene.

## Why This Project Matters

This project demonstrates:

* object-oriented design in a non-trivial system
* geometric reasoning (vectors, intersections)
* separation of rendering pipeline components
* performance optimization using spatial data structures
* building a complete system from math to visual output

## Future Improvements

* reflections and refractions
* anti-aliasing
* global illumination
* better BVH splitting heuristics
* support for more file formats

## License

This project is licensed under the **GNU General Public License v3.0 (GPL-3.0)**.

See the LICENSE file for details.
