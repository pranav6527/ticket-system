import { ButtonHTMLAttributes, forwardRef } from 'react';

type ButtonProps = ButtonHTMLAttributes<HTMLButtonElement> & {
  variant?: 'primary' | 'secondary';
};

const Button = forwardRef<HTMLButtonElement, ButtonProps>(
  ({ variant = 'primary', className, ...props }, ref) => {
    const base =
      'inline-flex items-center justify-center rounded px-4 py-2 font-medium focus:outline-none focus:ring';

    const variants = {
      primary: 'bg-blue-500 text-white hover:bg-blue-600',
      secondary: 'bg-gray-200 text-gray-900 hover:bg-gray-300',
    };

    return (
      <button
        ref={ref}
        className={`${base} ${variants[variant]} ${className ?? ''}`}
        {...props}
      />
    );
  }
);

Button.displayName = 'Button';

export default Button;
