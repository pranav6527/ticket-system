// src/components/Posts.tsx
import { useGetPostsQuery, useCreatePostMutation, Post } from './postsApi';
import Button from '../../components/Button';

export default function Posts() {
  const { data, isLoading, error } = useGetPostsQuery();
  const [createPost, { isLoading: isCreating }] = useCreatePostMutation();

  if (isLoading) return <p>Loading...</p>;
  if (error) return <p>Error</p>;

  return (
    <div>
      <Button
        disabled={isCreating}
        onClick={() =>
          createPost({
            title: 'New Post',
            body: 'Hello world',
            userId: '1',
          })
        }
      >
        Add Post
      </Button>

      {data?.slice(0, 5).map((post: Post) => (
        <div key={post.id}>
          <h4>{post.title}</h4>
          <p>{post.body}</p>
        </div>
      ))}
    </div>
  );
}
