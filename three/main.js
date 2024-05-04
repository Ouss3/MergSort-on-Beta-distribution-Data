import { Scene, PerspectiveCamera, WebGLRenderer, BoxGeometry, MeshBasicMaterial, Mesh } from 'three';

// Create a scene
const scene = new Scene();

// Create a camera
const camera = new PerspectiveCamera(75, window.innerWidth / window.innerHeight, 0.1, 1000);
camera.position.z = 5;

// Create a renderer
const renderer = new WebGLRenderer();
renderer.setSize(window.innerWidth, window.innerHeight);

// Append the renderer to the body
document.body.appendChild(renderer.domElement);

// Create a BoxGeometry
const geometry = new BoxGeometry(1, 1, 1);

// Create a MeshBasicMaterial
const material = new MeshBasicMaterial({ color: 0x00ff00 });

// Create a Mesh and add it to the scene
const cube = new Mesh(geometry, material);
scene.add(cube);

// Animation loop
function animate() {
    requestAnimationFrame(animate);

    // Rotate the cube
    cube.rotation.x += 0.01;
    cube.rotation.y += 0.01;

    renderer.render(scene, camera);
}
animate();