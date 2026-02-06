import { test, expect, type Page } from '@playwright/test';

type Ticket = {
  id: number;
  subject: string;
  description: string;
  status: string;
};

type SessionMocks = {
  loginStatus?: number;
  signupStatus?: number;
  role?: 'USER' | 'ADMIN';
  initialTickets?: Ticket[];
};

const buildToken = (payload: Record<string, unknown>) => {
  const header = Buffer.from(
    JSON.stringify({ alg: 'none', typ: 'JWT' })
  ).toString('base64url');
  const body = Buffer.from(JSON.stringify(payload)).toString('base64url');
  return `${header}.${body}.signature`;
};

const setupSessionMocks = async (
  page: Page,
  {
    loginStatus = 200,
    signupStatus = 200,
    role = 'USER',
    initialTickets = [],
  }: SessionMocks = {}
) => {
  const token = buildToken({ role });
  const tickets = [...initialTickets];
  const createdPayloads: Array<{ subject: string; description: string }> = [];
  let logoutCalls = 0;

  await page.route('**/api/auth/login', async (route) => {
    if (loginStatus !== 200) {
      await route.fulfill({
        status: loginStatus,
        contentType: 'application/json',
        body: JSON.stringify({ message: 'Invalid credentials' }),
      });
      return;
    }

    await route.fulfill({
      status: 200,
      contentType: 'application/json',
      headers: {
        'Set-Cookie': 'refreshToken=fake; Path=/; HttpOnly',
      },
      body: JSON.stringify({ accessToken: token }),
    });
  });

  await page.route('**/api/auth/signup', async (route) => {
    if (signupStatus !== 200) {
      await route.fulfill({
        status: signupStatus,
        contentType: 'application/json',
        body: JSON.stringify({ message: 'Signup failed' }),
      });
      return;
    }

    await route.fulfill({
      status: 200,
      contentType: 'application/json',
      headers: {
        'Set-Cookie': 'refreshToken=fake; Path=/; HttpOnly',
      },
      body: JSON.stringify({ accessToken: token }),
    });
  });

  await page.route('**/api/auth/logout', async (route) => {
    logoutCalls += 1;
    await route.fulfill({
      status: 200,
      contentType: 'application/json',
      body: JSON.stringify({ message: 'ok' }),
    });
  });

  await page.route('**/api/tickets', async (route) => {
    if (route.request().method() === 'GET') {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify(tickets),
      });
      return;
    }

    if (route.request().method() === 'POST') {
      const payload = (await route.request().postDataJSON()) as {
        subject: string;
        description: string;
      };

      createdPayloads.push(payload);
      tickets.push({
        id: tickets.length + 1,
        subject: payload.subject,
        description: payload.description,
        status: 'OPEN',
      });

      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({}),
      });
      return;
    }

    await route.fulfill({ status: 405, body: '' });
  });

  return { createdPayloads, getLogoutCalls: () => logoutCalls };
};

test.describe('@smoke frontend auth + tickets', () => {
  test('root path redirects to login', async ({ page }) => {
    await page.goto('/');

    await expect(page).toHaveURL(/\/login$/);
    await expect(page.getByRole('heading', { name: 'Welcome' })).toBeVisible();
  });

  test('tickets route redirects to login when not authenticated', async ({
    page,
  }) => {
    await page.goto('/tickets');

    await expect(page).toHaveURL(/\/login$/);
    await expect(page.getByRole('button', { name: 'Login' })).toBeVisible();
  });

  test('login validation errors are shown for empty form', async ({ page }) => {
    await page.goto('/login');
    await page.getByRole('button', { name: 'Login' }).click();

    await expect(page.getByText('Email is required')).toBeVisible();
    await expect(page.getByText('Password is required')).toBeVisible();
  });

  test('login failure shows invalid credentials message', async ({ page }) => {
    await setupSessionMocks(page, { loginStatus: 400 });

    await page.goto('/login');
    await page.getByPlaceholder('Email address').fill('user@example.com');
    await page.getByPlaceholder('Password').fill('WrongPass!');
    await page.getByRole('button', { name: 'Login' }).click();

    await expect(page.getByText('Invalid email or password')).toBeVisible();
    await expect(page).toHaveURL(/\/login$/);
  });

  test('user can sign up and land on tickets page', async ({ page }) => {
    await setupSessionMocks(page, {
      initialTickets: [
        {
          id: 1,
          subject: 'Welcome ticket',
          description: 'Start here',
          status: 'OPEN',
        },
      ],
    });

    await page.goto('/signup');
    await page.getByPlaceholder('Email address').fill('new.user@example.com');
    await page.getByPlaceholder('Password').fill('Pass123!');
    await page.getByRole('button', { name: 'Sign Up' }).click();

    await expect(page).toHaveURL(/\/tickets$/);
    await expect(page.getByText('Welcome ticket')).toBeVisible();
  });

  test('user can login, create a ticket, and logout', async ({ page }) => {
    const { createdPayloads, getLogoutCalls } = await setupSessionMocks(page, {
      initialTickets: [
        {
          id: 1,
          subject: 'Need help',
          description: 'Something broke',
          status: 'OPEN',
        },
      ],
    });

    await page.goto('/login');
    await page.getByPlaceholder('Email address').fill('user@example.com');
    await page.getByPlaceholder('Password').fill('Pass123!');
    await page.getByRole('button', { name: 'Login' }).click();

    await expect(page.getByText('My Tickets')).toBeVisible();
    await expect(page.getByText('Need help')).toBeVisible();

    await page.getByPlaceholder('Subject').fill('Printer issue');
    await page.getByPlaceholder('Description').fill('Office printer is jammed');
    await page.getByRole('button', { name: 'Add Ticket' }).click();

    await expect(page.getByText('Printer issue')).toBeVisible();
    expect(createdPayloads).toEqual([
      {
        subject: 'Printer issue',
        description: 'Office printer is jammed',
      },
    ]);

    await page.getByRole('button', { name: 'Logout' }).click();

    await expect(page).toHaveURL(/\/login$/);
    await expect(page.getByRole('heading', { name: 'Welcome' })).toBeVisible();
    expect(getLogoutCalls()).toBe(1);
  });
});
