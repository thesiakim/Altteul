import { SyncLoader } from 'react-spinners';

const LoadingSpinner = ({ loading }: { loading: boolean }) => {
  return (
    <div className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50">
      <SyncLoader color="#E06C2D" margin={5} size={15} />
    </div>
  );
};

export default LoadingSpinner;
