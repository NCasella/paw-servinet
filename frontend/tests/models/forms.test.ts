import { describe, it, expect } from 'vitest';
import {
    ServiceCreateSchema,
    ServiceUpdateSchema,
    ServiceForm
} from '../../src/models/forms/ServiceCreationForm';
import {
    RegisterUserForm,
    RegisterUserFormSchema
} from '../../src/models/forms/UserCreationForm';
import {
    UserUpdateForm,
    UserUpdateFormSchema
} from '../../src/models/forms/UserUpdateForm';

// =============================================================================
// ServiceCreateSchema Tests
// =============================================================================

describe('ServiceCreateSchema', () => {
    const validServiceData = {
        serviceName: 'Haircut Service',
        description: 'A professional haircut',
        homeService: true,
        neighbourhoods: ['Palermo', 'Caballito'],
        address: 'Gurruchaga 1234',
        price: '50.00',
        additionalCosts: false,
        pricingType: 'PER_TOTAL',
        category: 'PELUQUERIA',
        minimalDuration: 30
    };

    it('passes for valid data', () => {
        const result = ServiceCreateSchema.safeParse(validServiceData);
        expect(result.success).toBe(true);
    });

    // serviceName tests
    it('fails when serviceName is empty', () => {
        const data = { ...validServiceData, serviceName: '' };
        const result = ServiceCreateSchema.safeParse(data);
        expect(result.success).toBe(false);
        if (!result.success) {
            const serviceNameError = result.error.issues.find(i => i.path[0] === 'serviceName');
            expect(serviceNameError?.message).toBe('NotEmpty.serviceForm.title');
        }
    });

    it('fails when serviceName is too long (>255)', () => {
        const data = { ...validServiceData, serviceName: 'A'.repeat(256) };
        const result = ServiceCreateSchema.safeParse(data);
        expect(result.success).toBe(false);
        if (!result.success) {
            const serviceNameError = result.error.issues.find(i => i.path[0] === 'serviceName');
            expect(serviceNameError?.message).toBe('Size.serviceForm.title');
        }
    });

    // description tests
    it('passes when description is optional/empty', () => {
        const data = { ...validServiceData, description: '' };
        const result = ServiceCreateSchema.safeParse(data);
        expect(result.success).toBe(true);
    });

    it('passes when description is undefined', () => {
        const { description, ...dataWithoutDesc } = validServiceData;
        const result = ServiceCreateSchema.safeParse(dataWithoutDesc);
        expect(result.success).toBe(true);
    });

    it('fails when description is too long', () => {
        const data = { ...validServiceData, description: 'A'.repeat(256) };
        const result = ServiceCreateSchema.safeParse(data);
        expect(result.success).toBe(false);
        if (!result.success) {
            const descError = result.error.issues.find(i => i.path[0] === 'description');
            expect(descError?.message).toBe('Size.serviceForm.description');
        }
    });

    // homeService tests
    it('fails when homeService is missing', () => {
        const { homeService, ...dataWithoutHomeService } = validServiceData;
        const result = ServiceCreateSchema.safeParse(dataWithoutHomeService);
        expect(result.success).toBe(false);
    });

    it('passes when homeService is true', () => {
        const data = { ...validServiceData, homeService: true };
        const result = ServiceCreateSchema.safeParse(data);
        expect(result.success).toBe(true);
    });

    it('passes when homeService is false', () => {
        const data = { ...validServiceData, homeService: false };
        const result = ServiceCreateSchema.safeParse(data);
        expect(result.success).toBe(true);
    });

    // neighbourhoods tests
    it('fails when neighbourhoods array is empty', () => {
        const data = { ...validServiceData, neighbourhoods: [] };
        const result = ServiceCreateSchema.safeParse(data);
        expect(result.success).toBe(false);
        if (!result.success) {
            const neighError = result.error.issues.find(i => i.path[0] === 'neighbourhoods');
            expect(neighError?.message).toBe('NotEmpty.serviceForm.neighbourhoods');
        }
    });

    // address tests
    it('fails when address is empty and homeService is false', () => {
        const data = { ...validServiceData, homeService: false, address: '' };
        const result = ServiceCreateSchema.safeParse(data);
        expect(result.success).toBe(false);
        if (!result.success) {
            const addrError = result.error.issues.find(i => i.path[0] === 'address');
            expect(addrError?.message).toBe('NotEmpty.appointmentForm.location');
        }
    });

    it('passes when address is empty and homeService is true', () => {
        const data = { ...validServiceData, homeService: true, address: '' };
        const result = ServiceCreateSchema.safeParse(data);
        expect(result.success).toBe(true);
    });

    // price regex validation tests
    it('passes for valid price format (integer)', () => {
        const data = { ...validServiceData, price: '100' };
        const result = ServiceCreateSchema.safeParse(data);
        expect(result.success).toBe(true);
    });

    it('passes for valid price format (decimal with 1 digit)', () => {
        const data = { ...validServiceData, price: '100.5' };
        const result = ServiceCreateSchema.safeParse(data);
        expect(result.success).toBe(true);
    });

    it('passes for valid price format (decimal with 2 digits)', () => {
        const data = { ...validServiceData, price: '100.50' };
        const result = ServiceCreateSchema.safeParse(data);
        expect(result.success).toBe(true);
    });

    it('fails for invalid price format (3 decimal digits)', () => {
        const data = { ...validServiceData, price: '100.500' };
        const result = ServiceCreateSchema.safeParse(data);
        expect(result.success).toBe(false);
        if (!result.success) {
            const priceError = result.error.issues.find(i => i.path[0] === 'price');
            expect(priceError?.message).toBe('Size.serviceForm.price');
        }
    });

    it('fails for invalid price format (letters)', () => {
        const data = { ...validServiceData, price: 'abc' };
        const result = ServiceCreateSchema.safeParse(data);
        expect(result.success).toBe(false);
    });

    // price required when pricingType != TBD
    it('fails when price is null/empty and pricingType is not TBD', () => {
        const data = { ...validServiceData, price: null, pricingType: 'FIXED' };
        const result = ServiceCreateSchema.safeParse(data);
        expect(result.success).toBe(false);
        if (!result.success) {
            const priceError = result.error.issues.find(i => i.path[0] === 'price');
            expect(priceError?.message).toBe('NotEmpty.serviceForm.price');
        }
    });

    it('fails when price is empty string and pricingType is not TBD', () => {
        const data = { ...validServiceData, price: '', pricingType: 'FIXED' };
        const result = ServiceCreateSchema.safeParse(data);
        expect(result.success).toBe(false);
        if (!result.success) {
            const priceError = result.error.issues.find(i => i.path[0] === 'price');
            expect(priceError?.message).toBe('NotEmpty.serviceForm.price');
        }
    });

    // price should be null/empty when pricingType = TBD
    it('passes when price is null and pricingType is TBD', () => {
        const data = { ...validServiceData, price: null, pricingType: 'TBD' };
        const result = ServiceCreateSchema.safeParse(data);
        expect(result.success).toBe(true);
    });

    it('passes when price is empty string and pricingType is TBD', () => {
        const data = { ...validServiceData, price: '', pricingType: 'TBD' };
        const result = ServiceCreateSchema.safeParse(data);
        expect(result.success).toBe(true);
    });

    it('fails when price has value and pricingType is TBD', () => {
        const data = { ...validServiceData, price: '50.00', pricingType: 'TBD' };
        const result = ServiceCreateSchema.safeParse(data);
        expect(result.success).toBe(false);
        if (!result.success) {
            const priceError = result.error.issues.find(i => i.path[0] === 'price');
            expect(priceError?.message).toBe('Size.serviceForm.price');
        }
    });

    // pricingType tests
    it('fails when pricingType is empty', () => {
        const data = { ...validServiceData, pricingType: '' };
        const result = ServiceCreateSchema.safeParse(data);
        expect(result.success).toBe(false);
        if (!result.success) {
            const pricingError = result.error.issues.find(i => i.path[0] === 'pricingType');
            expect(pricingError?.message).toBe('NotEmpty.serviceForm.pricingType');
        }
    });

    // category tests
    it('fails when category is empty', () => {
        const data = { ...validServiceData, category: '' };
        const result = ServiceCreateSchema.safeParse(data);
        expect(result.success).toBe(false);
        if (!result.success) {
            const catError = result.error.issues.find(i => i.path[0] === 'category');
            expect(catError?.message).toBe('NotEmpty.serviceForm.category');
        }
    });

    // minimalDuration tests
    it('fails when minimalDuration is zero', () => {
        const data = { ...validServiceData, minimalDuration: 0 };
        const result = ServiceCreateSchema.safeParse(data);
        expect(result.success).toBe(false);
        if (!result.success) {
            const durError = result.error.issues.find(i => i.path[0] === 'minimalDuration');
            expect(durError?.message).toBe('Positive.serviceForm.minimalduration');
        }
    });

    it('fails when minimalDuration is negative', () => {
        const data = { ...validServiceData, minimalDuration: -5 };
        const result = ServiceCreateSchema.safeParse(data);
        expect(result.success).toBe(false);
        if (!result.success) {
            const durError = result.error.issues.find(i => i.path[0] === 'minimalDuration');
            expect(durError?.message).toBe('Positive.serviceForm.minimalduration');
        }
    });

    it('passes when minimalDuration is positive integer', () => {
        const data = { ...validServiceData, minimalDuration: 60 };
        const result = ServiceCreateSchema.safeParse(data);
        expect(result.success).toBe(true);
    });
});

// =============================================================================
// ServiceUpdateSchema Tests
// =============================================================================

describe('ServiceUpdateSchema', () => {
    const validUpdateData = {
        description: 'Updated description',
        pricingType: 'FIXED',
        price: '75.00',
        additionalCosts: true,
        minimalDuration: 45
    };

    it('passes for valid data', () => {
        const result = ServiceUpdateSchema.safeParse(validUpdateData);
        expect(result.success).toBe(true);
    });

    it('fails when price is empty and pricingType is not TBD', () => {
        const data = { ...validUpdateData, price: '', pricingType: 'FIXED' };
        const result = ServiceUpdateSchema.safeParse(data);
        expect(result.success).toBe(false);
        if (!result.success) {
            const priceError = result.error.issues.find(i => i.path[0] === 'price');
            expect(priceError?.message).toBe('NotEmpty.serviceForm.price');
        }
    });

    it('fails when price has value and pricingType is TBD', () => {
        const data = { ...validUpdateData, price: '50.00', pricingType: 'TBD' };
        const result = ServiceUpdateSchema.safeParse(data);
        expect(result.success).toBe(false);
        if (!result.success) {
            const priceError = result.error.issues.find(i => i.path[0] === 'price');
            expect(priceError?.message).toBe('Size.serviceForm.price');
        }
    });

    it('passes when price is null and pricingType is TBD', () => {
        const data = { ...validUpdateData, price: null, pricingType: 'TBD' };
        const result = ServiceUpdateSchema.safeParse(data);
        expect(result.success).toBe(true);
    });

    it('fails when pricingType is empty', () => {
        const data = { ...validUpdateData, pricingType: '' };
        const result = ServiceUpdateSchema.safeParse(data);
        expect(result.success).toBe(false);
    });

    it('fails when minimalDuration is not positive', () => {
        const data = { ...validUpdateData, minimalDuration: 0 };
        const result = ServiceUpdateSchema.safeParse(data);
        expect(result.success).toBe(false);
    });
});

// =============================================================================
// ServiceForm Class Tests
// =============================================================================

describe('ServiceForm', () => {
    describe('validateServiceCreateForm', () => {
        it('returns empty errors for valid form', () => {
            const form = new ServiceForm({
                serviceName: 'Test Service',
                description: 'Description',
                homeService: false,
                neighbourhoods: ['Palermo'],
                address: 'Gurruchaga 1234',
                price: '100.00',
                pricingType: 'PER_TOTAL',
                category: 'PELUQUERIA',
                minimalDuration: 30
            });

            const errors = form.validateServiceCreateForm();
            expect(Object.keys(errors).length).toBe(0);
        });

        it('returns errors for invalid form', () => {
            const form = new ServiceForm({
                serviceName: '',
                homeService: false,
                neighbourhoods: [],
                address: '',
                price: null,
                pricingType: 'PER_HOUR',
                category: '',
                minimalDuration: 0
            });

            const errors = form.validateServiceCreateForm();
            expect(errors.serviceName).toBe('NotEmpty.serviceForm.title');
            expect(errors.neighbourhoods).toBe('NotEmpty.serviceForm.neighbourhoods');
            expect(errors.category).toBe('NotEmpty.serviceForm.category');
            expect(errors.minimalDuration).toBe('Positive.serviceForm.minimalduration');
        });
    });

    describe('validateServiceUpdateForm', () => {
        it('returns empty errors for valid update form', () => {
            const form = new ServiceForm({
                description: 'Updated',
                pricingType: 'PER_TOTAL',
                price: '50.00',
                additionalCosts: false,
                minimalDuration: 60
            });

            const errors = form.validateServiceUpdateForm();
            expect(Object.keys(errors).length).toBe(0);
        });

        it('returns errors for invalid update form', () => {
            const form = new ServiceForm({
                pricingType: '',
                price: '50.00',
                additionalCosts: false,
                minimalDuration: -1
            });

            const errors = form.validateServiceUpdateForm();
            expect(errors.pricingType).toBe('NotEmpty.serviceForm.pricingType');
            expect(errors.minimalDuration).toBe('Positive.serviceForm.minimalduration');
        });
    });
});

// =============================================================================
// RegisterUserFormSchema Tests
// =============================================================================

describe('RegisterUserFormSchema', () => {
    const validUserData = {
        name: 'John',
        surname: 'Doe',
        email: 'john.doe@example.com',
        telephone: '+1 9 1234 123456',
        username: 'johndoe',
        password: 'password123',
        confirmPassword: 'password123'
    };

    it('passes for valid data', () => {
        const result = RegisterUserFormSchema.safeParse(validUserData);
        expect(result.success).toBe(true);
    });

    // name tests
    it('fails when name is empty', () => {
        const data = { ...validUserData, name: '' };
        const result = RegisterUserFormSchema.safeParse(data);
        expect(result.success).toBe(false);
        if (!result.success) {
            const nameError = result.error.issues.find(i => i.path[0] === 'name');
            expect(nameError?.message).toBe('NotEmpty.registerUserForm.name');
        }
    });

    it('fails when name is too long', () => {
        const data = { ...validUserData, name: 'A'.repeat(256) };
        const result = RegisterUserFormSchema.safeParse(data);
        expect(result.success).toBe(false);
        if (!result.success) {
            const nameError = result.error.issues.find(i => i.path[0] === 'name');
            expect(nameError?.message).toBe('Size.registerUserForm.name');
        }
    });

    // surname tests
    it('fails when surname is empty', () => {
        const data = { ...validUserData, surname: '' };
        const result = RegisterUserFormSchema.safeParse(data);
        expect(result.success).toBe(false);
        if (!result.success) {
            const surnameError = result.error.issues.find(i => i.path[0] === 'surname');
            expect(surnameError?.message).toBe('NotEmpty.registerUserForm.surname');
        }
    });

    // email tests
    it('fails when email is empty', () => {
        const data = { ...validUserData, email: '' };
        const result = RegisterUserFormSchema.safeParse(data);
        expect(result.success).toBe(false);
        if (!result.success) {
            const emailError = result.error.issues.find(i => i.path[0] === 'email');
            expect(emailError?.message).toBe('NotEmpty.registerUserForm.email');
        }
    });

    it('fails when email format is invalid', () => {
        const data = { ...validUserData, email: 'not-an-email' };
        const result = RegisterUserFormSchema.safeParse(data);
        expect(result.success).toBe(false);
        if (!result.success) {
            const emailError = result.error.issues.find(i => i.path[0] === 'email');
            expect(emailError?.message).toBe('Email.registerUserForm.email');
        }
    });

    // telephone tests
    it('passes for valid telephone format (+1 9 1234 123456)', () => {
        const data = { ...validUserData, telephone: '+1 9 1234 123456' };
        const result = RegisterUserFormSchema.safeParse(data);
        expect(result.success).toBe(true);
    });

    it('passes for valid telephone format without spaces', () => {
        const data = { ...validUserData, telephone: '+549111234567' };
        const result = RegisterUserFormSchema.safeParse(data);
        expect(result.success).toBe(true);
    });

    it('fails for invalid telephone format', () => {
        const data = { ...validUserData, telephone: '123456' };
        const result = RegisterUserFormSchema.safeParse(data);
        expect(result.success).toBe(false);
        if (!result.success) {
            const telError = result.error.issues.find(i => i.path[0] === 'telephone');
            expect(telError?.message).toBe('Pattern.registerUserForm.telephone');
        }
    });

    it('fails when telephone is empty', () => {
        const data = { ...validUserData, telephone: '' };
        const result = RegisterUserFormSchema.safeParse(data);
        expect(result.success).toBe(false);
        if (!result.success) {
            const telError = result.error.issues.find(i => i.path[0] === 'telephone');
            expect(telError?.message).toBe('NotEmpty.registerUserForm.telephone');
        }
    });

    // username tests
    it('fails when username is empty', () => {
        const data = { ...validUserData, username: '' };
        const result = RegisterUserFormSchema.safeParse(data);
        expect(result.success).toBe(false);
        if (!result.success) {
            const usernameError = result.error.issues.find(i => i.path[0] === 'username');
            expect(usernameError?.message).toBe('NotEmpty.registerUserForm.username');
        }
    });

    // password tests
    it('fails when password is less than 8 characters', () => {
        const data = { ...validUserData, password: 'short', confirmPassword: 'short' };
        const result = RegisterUserFormSchema.safeParse(data);
        expect(result.success).toBe(false);
        if (!result.success) {
            const pwError = result.error.issues.find(i => i.path[0] === 'password');
            expect(pwError?.message).toBe('Size.registerUserForm.password');
        }
    });

    it('passes when password is exactly 8 characters', () => {
        const data = { ...validUserData, password: '12345678', confirmPassword: '12345678' };
        const result = RegisterUserFormSchema.safeParse(data);
        expect(result.success).toBe(true);
    });

    // confirmPassword tests
    it('fails when confirmPassword does not match password', () => {
        const data = { ...validUserData, password: 'password123', confirmPassword: 'differentpassword' };
        const result = RegisterUserFormSchema.safeParse(data);
        expect(result.success).toBe(false);
        if (!result.success) {
            const confirmError = result.error.issues.find(i => i.path.includes('confirmPassword'));
            expect(confirmError?.message).toBe('FieldsValueMatch.registerUserForm.password');
        }
    });

    it('passes when confirmPassword matches password', () => {
        const data = { ...validUserData, password: 'mypassword123', confirmPassword: 'mypassword123' };
        const result = RegisterUserFormSchema.safeParse(data);
        expect(result.success).toBe(true);
    });
});

// =============================================================================
// RegisterUserForm Class Tests
// =============================================================================

describe('RegisterUserForm', () => {
    describe('validateRegisterUserForm', () => {
        it('returns empty errors for valid form', () => {
            const form = new RegisterUserForm(
                'john@example.com',
                'John',
                'Doe',
                '+54 9 1234 123456',
                'johndoe',
                'password123',
                'password123'
            );

            const errors = form.validateRegisterUserForm();
            expect(Object.keys(errors).length).toBe(0);
        });

        it('returns errors for invalid form', () => {
            const form = new RegisterUserForm(
                'invalid-email',
                '',
                '',
                '123',
                '',
                'short',
                'mismatch'
            );

            const errors = form.validateRegisterUserForm();
            expect(errors.name).toBe('NotEmpty.registerUserForm.name');
            expect(errors.surname).toBe('NotEmpty.registerUserForm.surname');
            expect(errors.email).toBe('Email.registerUserForm.email');
            expect(errors.telephone).toBe('Pattern.registerUserForm.telephone');
            expect(errors.username).toBe('NotEmpty.registerUserForm.username');
            expect(errors.password).toBe('Size.registerUserForm.password');
        });
    });
});

// =============================================================================
// UserUpdateFormSchema Tests
// =============================================================================

describe('UserUpdateFormSchema', () => {
    const validUpdateUserData = {
        username: 'johndoe',
        email: 'john@example.com',
        telephone: '+54 9 1234 123456',
        locale: 'en',
        password: ''
    };

    it('passes for valid data', () => {
        const result = UserUpdateFormSchema.safeParse(validUpdateUserData);
        expect(result.success).toBe(true);
    });

    // username tests
    it('fails when username is empty', () => {
        const data = { ...validUpdateUserData, username: '' };
        const result = UserUpdateFormSchema.safeParse(data);
        expect(result.success).toBe(false);
        if (!result.success) {
            const usernameError = result.error.issues.find(i => i.path[0] === 'username');
            expect(usernameError?.message).toBe('NotEmpty.registerUserForm.username');
        }
    });

    // email tests
    it('fails when email format is invalid', () => {
        const data = { ...validUpdateUserData, email: 'not-an-email' };
        const result = UserUpdateFormSchema.safeParse(data);
        expect(result.success).toBe(false);
        if (!result.success) {
            const emailError = result.error.issues.find(i => i.path[0] === 'email');
            expect(emailError?.message).toBe('Email.registerUserForm.email');
        }
    });

    it('passes for valid email with special characters', () => {
        const data = { ...validUpdateUserData, email: 'john.doe+test@example.co.uk' };
        const result = UserUpdateFormSchema.safeParse(data);
        expect(result.success).toBe(true);
    });

    // telephone tests
    it('fails when telephone format is invalid', () => {
        const data = { ...validUpdateUserData, telephone: 'not-a-phone' };
        const result = UserUpdateFormSchema.safeParse(data);
        expect(result.success).toBe(false);
        if (!result.success) {
            const telError = result.error.issues.find(i => i.path[0] === 'telephone');
            expect(telError?.message).toBe('Pattern.registerUserForm.telephone');
        }
    });

    it('passes for valid telephone format', () => {
        const data = { ...validUpdateUserData, telephone: '+1 9 1234 123456' };
        const result = UserUpdateFormSchema.safeParse(data);
        expect(result.success).toBe(true);
    });

    // locale tests
    it('passes when locale is "en"', () => {
        const data = { ...validUpdateUserData, locale: 'en' };
        const result = UserUpdateFormSchema.safeParse(data);
        expect(result.success).toBe(true);
    });

    it('passes when locale is "es"', () => {
        const data = { ...validUpdateUserData, locale: 'es' };
        const result = UserUpdateFormSchema.safeParse(data);
        expect(result.success).toBe(true);
    });

    it('fails when locale is not "en" or "es"', () => {
        const data = { ...validUpdateUserData, locale: 'fr' };
        const result = UserUpdateFormSchema.safeParse(data);
        expect(result.success).toBe(false);
        if (!result.success) {
            const localeError = result.error.issues.find(i => i.path[0] === 'locale');
            expect(localeError?.message).toBe('Pattern.registerUserForm.locale');
        }
    });

    it('fails when locale is empty', () => {
        const data = { ...validUpdateUserData, locale: '' };
        const result = UserUpdateFormSchema.safeParse(data);
        expect(result.success).toBe(false);
    });
});

// =============================================================================
// UserUpdateForm Class Tests
// =============================================================================

describe('UserUpdateForm', () => {
    describe('validate', () => {
        it('returns empty errors for valid form', () => {
            const form = new UserUpdateForm({
                username: 'johndoe',
                email: 'john@example.com',
                telephone: '+54 9 1234 123456',
                locale: 'es',
                password: ''
            });

            const errors = form.validate();
            expect(Object.keys(errors).length).toBe(0);
        });

        it('returns errors for invalid form', () => {
            const form = new UserUpdateForm({
                username: '',
                email: 'invalid-email',
                telephone: '123',
                locale: 'fr',
                password: ''
            });

            const errors = form.validate();
            expect(errors.username).toBe('NotEmpty.registerUserForm.username');
            expect(errors.email).toBe('Email.registerUserForm.email');
            expect(errors.telephone).toBe('Pattern.registerUserForm.telephone');
            expect(errors.locale).toBe('Pattern.registerUserForm.locale');
        });
    });
});
