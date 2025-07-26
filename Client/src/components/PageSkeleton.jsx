import Skeleton from 'react-loading-skeleton';
import 'react-loading-skeleton/dist/skeleton.css';

export default function PageSkeleton() {
  return (
    <div style={{ padding: '2rem' }}>
      <Skeleton height={40} width={300} style={{ marginBottom: '1rem' }} />
      <Skeleton count={4} height={20} style={{ marginBottom: '0.5rem' }} />
      <Skeleton height={200} style={{ marginTop: '2rem' }} />
    </div>
  );
}
